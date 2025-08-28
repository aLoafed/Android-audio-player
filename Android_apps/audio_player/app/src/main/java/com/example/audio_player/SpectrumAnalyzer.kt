package com.example.audio_player

import androidx.compose.runtime.Composable
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import org.jtransforms.fft.DoubleFFT_1D
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import kotlin.math.sqrt

@UnstableApi
class SpectrumAnalyzer : AudioProcessor {
    private var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
    private var fft = DoubleFFT_1D(512) // Creates instance
    private var endOfStreamQueued = false
    private var isEnded = false

    var eqList = DoubleArray(7)

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        return inputAudioFormat
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        outputBuffer = inputBuffer
        if (!inputBuffer.hasRemaining()) {
            return
        }
        val shortBuffer = inputBuffer.asShortBuffer()
        val fftArray = DoubleArray(512) //512 as it's a power of 2 and isn't too laggy

        for (i in 0..511) {
            try {
                fftArray[i] = shortBuffer.get() / 32768.0 // Normalisation
            } catch (e: BufferUnderflowException) {
                fftArray[i] = 0.0
            }
            if (fftArray[i].isNaN() or fftArray[i].isInfinite()) { // Prevent float NaN's
                fftArray[i] = 0.0
            }
            val window = 0.5 * (1 - kotlin.math.cos(2.0 * Math.PI * i / (512 - 1))) // Hann window to reduce sound leakage
            fftArray[i] = fftArray[i] * window
        }


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
        tempList[0] = (absValueList[1])
        tempList[1] = (absValueList[2])
        tempList[2] = (absValueList[5])
        tempList[3] = (absValueList[13])
        tempList[4] = (absValueList[32])
        tempList[5] = (absValueList[80])
        tempList[6] = (absValueList[205])
        return tempList

    }
}