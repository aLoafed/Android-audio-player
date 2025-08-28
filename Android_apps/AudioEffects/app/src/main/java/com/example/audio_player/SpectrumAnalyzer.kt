package com.example.audio_player

import androidx.collection.mutableDoubleListOf
import androidx.compose.runtime.Composable
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.PitchShifter
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.TarsosDSPAudioInputStream
import be.tarsos.dsp.util.PitchConverter
import org.jtransforms.fft.DoubleFFT_1D
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import kotlin.math.sqrt

@UnstableApi
class SpectrumAnalyzer : AudioProcessor {
    private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
    private var fft = DoubleFFT_1D(1024) // Creates instance
    private var endOfStreamQueued = false
    private var isEnded = false
    private var sampleRate: Int = 44100
    private var channels: Int = 1

    var eqList = DoubleArray(7)

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        sampleRate = inputAudioFormat.sampleRate
        return inputAudioFormat
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) {
            return
        }
        val shortBuffer = inputBuffer.asShortBuffer()
        val fftArray = DoubleArray(1024) //512 as it's a power of 2 and isn't too laggy

        for (i in 0..511) {
            try {
                fftArray[i] = shortBuffer.get() / 32768.0 // Normalisation
            } catch (e: BufferUnderflowException) {
                fftArray[i] = 0.0
            }
            if (fftArray[i].isNaN() or fftArray[i].isInfinite()) { // Prevent float NaN's
                fftArray[i] = 0.0
            }
            val window = 0.5 * (1 - kotlin.math.cos(2.0 * Math.PI * i / (1024 - 1))) // Hann window to reduce sound leakage
            fftArray[i] = fftArray[i] * window
        }
        val timeStretch = WaveformSimilarityBasedOverlapAdd( // Sets WSOLA time stretcher // tempo > 1 slows it down
            WaveformSimilarityBasedOverlapAdd.Parameters.slowdownDefaults(1.5, sampleRate.toDouble())
        )
        val pitchShifter = PitchShifter( // Sets pitch shifter
            -3.0,
            sampleRate.toDouble(),
            1024,
            512
        )
        val audioEvent = AudioEvent(
            TarsosDSPAudioFormat(
                sampleRate.toFloat(),
                16,
                channels,
                true,
                false
            )
        )

        val audioProcessingArray = FloatArray(1024)
        for (i in 0 until fftArray.count()) {
            audioProcessingArray[i] = fftArray[i].toFloat()
        }
        for (i in 0 until audioEvent.floatBuffer.size) {
            audioEvent.floatBuffer[i] = audioProcessingArray[i]
        }

        timeStretch.process(audioEvent) // Applies time stretch
        pitchShifter.process(audioEvent) // Applies pitch shifting
        val processed = audioEvent.floatBuffer // Grabs processed data
        val outBytes = ByteArray(processed.size * 2)

        var j = 0
        for (i in processed) {
            val s = (i * 32768).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
            outBytes[j++] = (s.toInt() and 0xFF).toByte()
            outBytes[j++] = ((s.toInt() shr 8) and 0xFF).toByte()
        }
        outputBuffer = ByteBuffer.wrap(outBytes)

        //================================= Graphical Equaliser =================================//
        fft.realForward(fftArray) // Input array has the output array

        val absValueList = DoubleArray(fftArray.count() / 2)
        var i = 0
        while (i < fftArray.count() / 2) {
            val real = fftArray[i * 2]
            val imaginary = fftArray[i * 2 + 1]
            absValueList[i] = sqrt(real * real + imaginary * imaginary)
            i++
        }
        eqList = frequencyCalculator(absValueList)
    }

    override fun queueEndOfStream() {
        endOfStreamQueued = true
    }

    override fun getOutput(): ByteBuffer {
        val result = outputBuffer
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        if (outputBuffer == AudioProcessor.EMPTY_BUFFER && endOfStreamQueued) {
            isEnded = true
        }
        return result
    }

    override fun isEnded(): Boolean {
        return isEnded
    }

    override fun flush() {
        isEnded = false
        endOfStreamQueued = false
        outputBuffer = AudioProcessor.EMPTY_BUFFER
    }

    override fun reset() {
        isEnded = false
        endOfStreamQueued = false
        outputBuffer = AudioProcessor.EMPTY_BUFFER
    }

    fun frequencyCalculator(absValueList: DoubleArray): DoubleArray {
        val tempList = DoubleArray(7)
        tempList[0] = (absValueList[1 * 2])
        tempList[1] = (absValueList[2 * 2])
        tempList[2] = (absValueList[5 * 2])
        tempList[3] = (absValueList[13 * 2])
        tempList[4] = (absValueList[32 * 2])
        tempList[5] = (absValueList[80 * 2])
        tempList[6] = (absValueList[205 * 2])
        return tempList

    }
}
class SimpleAudioEvent(
    var floatBuffer: FloatArray,
    val sampleRate: Float
) {
    val bufferSize: Int get() = floatBuffer.size
}
