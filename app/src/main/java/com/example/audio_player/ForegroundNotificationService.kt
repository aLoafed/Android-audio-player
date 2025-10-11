package com.example.audio_player

import android.content.Context
import android.os.Handler
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.Log
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
import org.jtransforms.fft.DoubleFFT_1D
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import kotlin.math.sqrt

@UnstableApi
class ForegroundNotificationService : MediaSessionService() {
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    object SpectrumAnalyzer : AudioProcessor {
        private val sonicAudioProcessor = SonicAudioProcessor()
        private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
        private var fft = DoubleFFT_1D(1024) // Creates FFT instance
        private var endOfStreamQueued = false
        private var isEnded = false
        var eqList = DoubleArray(7)
        var volume = 0.0
        var usingSonicProcessor = true // Is true for testing it atm
        override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
            if (usingSonicProcessor) {
                sonicAudioProcessor.configure(inputAudioFormat)
                // Factor must not be 1f or else null pointer exception
                sonicAudioProcessor.setPitch(0.8f)
                sonicAudioProcessor.setSpeed(0.8f)
            }
            return inputAudioFormat
        }

        override fun isActive(): Boolean {
            return true
        }

        override fun queueInput(inputBuffer: ByteBuffer) {
            // To counter the buffer from being consumed by sonic processor
            if (usingSonicProcessor) {
                sonicAudioProcessor.queueInput(inputBuffer)
            } else {
                outputBuffer = inputBuffer // Default audio buffer processing
            }
            if (!inputBuffer.hasRemaining()) {
                return
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
            //============================ Collecting buffer data ============================//
            val shortBuffer = result.asShortBuffer()// analysisBuffer.asShortBuffer()
            val fftArray = DoubleArray(1024) //512 as it's a power of 2 and isn't too laggy
            var bufferVolume = 0.0
            var buffer: Short
            for (i in 0 until 1024) {
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
                val window = 0.5 * (1 - kotlin.math.cos(2.0 * Math.PI * i / (1024 - 1))) // Hann window to reduce sound leakage
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
            bufferVolume = sqrt(bufferVolume / 1024)
            eqList = frequencyCalculator(absValueList)
            volume = bufferVolume
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
            val tempList = DoubleArray(7)
            tempList[0] = (absValueList[2])
            tempList[1] = (absValueList[4])
            tempList[2] = (absValueList[10])
            tempList[3] = (absValueList[26])
            tempList[4] = (absValueList[64])
            tempList[5] = (absValueList[160])
            tempList[6] = (absValueList[410])
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