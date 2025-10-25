package com.example.audio_player

import android.content.Context
import android.os.Handler
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jtransforms.fft.DoubleFFT_1D
import java.nio.BufferOverflowException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import kotlin.math.sin
import kotlin.math.sqrt

@UnstableApi
class PlayerService : MediaSessionService() {
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    object SpectrumAnalyzer : AudioProcessor {
        private val _eqStateFlow = MutableStateFlow(
            EQDataClass(
                doubleArrayOf(),
                0.0,
                1000L
            )
        )
        val eqStateFlow: StateFlow<EQDataClass> = _eqStateFlow.asStateFlow()
        var speed = 1f
        var pitch = 1f
        var equaliserIsOn = false
        private val sonicAudioProcessor = SonicAudioProcessor()
        private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
        private const val ARRAY_SIZE = 512
        private var fft = DoubleFFT_1D(ARRAY_SIZE.toLong()) // Creates FFT instance
        private var endOfStreamQueued = false
        private var isEnded = false
        var eqList = DoubleArray(7)
        var volume = 0.0
        var usingSonicProcessor = false

        override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
            if (usingSonicProcessor) {
                sonicAudioProcessor.configure(inputAudioFormat)
                // Factor must not be 1f or else null pointer exception
                if (speed != 1f) {
                    sonicAudioProcessor.setPitch(speed)
                }
                if (pitch != 1f) {
                    sonicAudioProcessor.setSpeed(pitch)
                }
            }
            return inputAudioFormat
        }

        override fun isActive(): Boolean {
            return true
        }

        override fun queueInput(inputBuffer: ByteBuffer) {
//            val size = inputBuffer.remaining()
//            val tmpOutBuffer = ByteBuffer
//                .allocate(size)
//                .order(inputBuffer.order())
//
//            val input = inputBuffer.duplicate()

//            while (input.remaining() >= 2) {
//                val sample = (input.getShort() * 1f)
//                    .toInt()
//                    .coerceIn(-32768, 32767)
//                    .toShort()
//                tmpOutBuffer.putShort(sample)
//            }
//            tmpOutBuffer.put(input)
//                .position(0)
//                .limit(size)
//            outputBuffer = tmpOutBuffer

            if (!inputBuffer.hasRemaining()) {
                return
            }
            if (usingSonicProcessor) {
                sonicAudioProcessor.queueInput(inputBuffer)
            } else {
                outputBuffer = inputBuffer // Default audio buffer processing
            }
        }

        override fun queueEndOfStream() {
            endOfStreamQueued = true
            sonicAudioProcessor.queueEndOfStream()
        }

        override fun getOutput(): ByteBuffer {
            val result = if (usingSonicProcessor) {
                sonicAudioProcessor.output
            } else {
                outputBuffer
            }
            if (equaliserIsOn) {
                //============================ Collecting buffer data ============================//
                val shortBuffer = result.asShortBuffer()// analysisBuffer.asShortBuffer()
                val fftArray = DoubleArray(ARRAY_SIZE) // 512 as it's a power of 2 and isn't too laggy // Current though laggy is 1024
                var bufferVolume = 0.0
                var buffer: Short
                for (i in 0 until ARRAY_SIZE) {
                    try {
                        buffer = shortBuffer.get()
                        bufferVolume += (buffer * buffer).toDouble() // To cancel out the - & + values
                        fftArray[i] = buffer / 32768.0 // Normalisation
                    } catch (e: BufferUnderflowException) {
                        fftArray[i] = 0.0
                        bufferVolume += 0.0
                    }
                    if (fftArray[i].isNaN() or fftArray[i].isInfinite()) { // Prevent float NaN's
                        fftArray[i] = 0.0
                        bufferVolume += 0.0
                    }
                    val window = 0.5 * (1 - kotlin.math.cos(2.0 * Math.PI * i / (ARRAY_SIZE - 1))) // Hann window to reduce sound leakage
                    fftArray[i] = fftArray[i] * window
                }
                //================================= Graphical equaliser data =================================//
                fft.realForward(fftArray) // Input array has the output array

                val absValueList = DoubleArray(fftArray.count() / 2)
                var i = 0
                while (i < fftArray.count() / 2) {
                    val real = fftArray[i * 2]
                    val imaginary = fftArray[i * 2 + 1]
                    absValueList[i] = sqrt(real * real + imaginary * imaginary)
                    i++
                }
                bufferVolume = sqrt(bufferVolume / ARRAY_SIZE)
                eqList = frequencyCalculator(absValueList)
                volume = bufferVolume

                val audioLatency = 1000L
                _eqStateFlow.tryEmit(
                    EQDataClass(
                        eqList,
                        volume,
                        audioLatency
                    )
                )
            }
            //================================= End of equaliser processing =================================//
            outputBuffer = AudioProcessor.EMPTY_BUFFER
            if (endOfStreamQueued) {
                isEnded = true
            }
            return result
        }

        override fun isEnded(): Boolean {
            return isEnded
        }

        override fun flush() {
            if (usingSonicProcessor) {
                sonicAudioProcessor.flush()
            }
            isEnded = false
            endOfStreamQueued = false
            outputBuffer = AudioProcessor.EMPTY_BUFFER
        }

        override fun reset() {
            if (usingSonicProcessor) {
                sonicAudioProcessor.reset()
            }
            isEnded = false
            endOfStreamQueued = false
            outputBuffer = AudioProcessor.EMPTY_BUFFER
        }
        fun frequencyCalculator(absValueList: DoubleArray): DoubleArray {
            // Div 2 to accommodate 512 fft size
            val tempList = DoubleArray(7)
            tempList[0] = (absValueList[2 / 2])
            tempList[1] = (absValueList[4 / 2])
            tempList[2] = (absValueList[10 / 2])
            tempList[3] = (absValueList[26 / 2])
            tempList[4] = (absValueList[64 / 2])
            tempList[5] = (absValueList[160 / 2])
            tempList[6] = (absValueList[410 / 2])
            return tempList
        }
    }

    fun getSpectrumAnalyzer(): SpectrumAnalyzer {
        return SpectrumAnalyzer
    }

    override fun onCreate() {
        super.onCreate()
        val myAudioSink = DefaultAudioSink.Builder(this)
            .setAudioProcessors(arrayOf(SpectrumAnalyzer))
            .build()
        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioRenderers(
                context: Context,
                extensionRendererMode: Int,
                mediaCodecSelector: MediaCodecSelector,
                enableDecoderFallback: Boolean,
                audioSink: AudioSink,
                eventHandler: Handler,
                eventListener: AudioRendererEventListener,
                out: ArrayList<Renderer>
            ) {
                super.buildAudioRenderers(
                    context,
                    extensionRendererMode,
                    mediaCodecSelector,
                    enableDecoderFallback,
                    myAudioSink,
                    eventHandler,
                    eventListener,
                    out
                )
                out.add(
                    MediaCodecAudioRenderer(
                        context,
                        mediaCodecSelector,
                        enableDecoderFallback,
                        eventHandler,
                        eventListener,
                        myAudioSink
                    )
                )
            }
        }
        player = ExoPlayer.Builder(this)
            .setRenderersFactory(renderersFactory)
            .build()
//        val mediaSessionCallback = object : MediaSession.Callback{}
        mediaSession = MediaSession.Builder(this, player)
            .build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}