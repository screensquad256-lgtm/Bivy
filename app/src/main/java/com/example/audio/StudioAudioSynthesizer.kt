package com.example.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

object StudioAudioSynthesizer {

    suspend fun prepareDemoAudioFiles(context: Context): Map<String, String> = withContext(Dispatchers.IO) {
        val pathMap = mutableMapOf<String, String>()
        val filesDir = context.filesDir

        val tracks = listOf(
            Triple("bivy_track_1", "orbital_resonance.wav", 1),
            Triple("bivy_track_2", "analog_horizon.wav", 2),
            Triple("bivy_track_3", "subterranean_pulse.wav", 3),
            Triple("bivy_track_4", "glass_rain.wav", 4),
            Triple("bivy_track_5", "velocity_zero.wav", 5)
        )

        for ((id, filename, style) in tracks) {
            val file = File(filesDir, filename)
            if (!file.exists() || file.length() < 1000) {
                generateWavFile(file, style)
            }
            pathMap[id] = file.absolutePath
        }

        pathMap
    }

    private fun generateWavFile(file: File, style: Int) {
        val sampleRate = 44100
        val durationSeconds = 16 // 16 seconds rich loopable audio
        val numSamples = sampleRate * durationSeconds
        val numChannels = 2 // Stereo
        val bytesPerSample = 2 // 16-bit PCM

        val pcmData = ByteArray(numSamples * numChannels * bytesPerSample)
        val buffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN)

        // Musical chord profiles for each style:
        // Style 1: Ambient Pad (D minor9: D3, F3, A3, C4, E4)
        // Style 2: Neo-Soul Warmth (Bb major7: Bb2, D3, F3, A3)
        // Style 3: Deep Bass Pulse (Sub 55Hz, 110Hz, 220Hz with tremolo)
        // Style 4: Glass Rain (Chimes / high harmonic bells: E5, G#5, B5, D#6)
        // Style 5: Synthwave Arp (A minor: 110Hz, 220Hz, 440Hz with phased sweep)

        val freqs = when (style) {
            1 -> doubleArrayOf(146.83, 174.61, 220.00, 261.63, 329.63)
            2 -> doubleArrayOf(116.54, 146.83, 174.61, 220.00)
            3 -> doubleArrayOf(55.00, 110.00, 164.81, 220.00)
            4 -> doubleArrayOf(659.25, 830.61, 987.77, 1244.51)
            else -> doubleArrayOf(110.00, 220.00, 329.63, 440.00)
        }

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = (sin(Math.PI * t / durationSeconds)).coerceIn(0.0, 1.0)

            var sampleL = 0.0
            var sampleR = 0.0

            when (style) {
                1 -> { // Orbital Resonance
                    for ((idx, f) in freqs.withIndex()) {
                        val phaseL = 2.0 * Math.PI * f * t
                        val phaseR = 2.0 * Math.PI * f * t + (idx * 0.4)
                        val amp = 0.18 / (idx + 1)
                        sampleL += amp * sin(phaseL) * (1.0 + 0.2 * sin(2.0 * Math.PI * 0.25 * t))
                        sampleR += amp * sin(phaseR) * (1.0 + 0.2 * sin(2.0 * Math.PI * 0.3 * t))
                    }
                }
                2 -> { // Analog Horizon
                    for ((idx, f) in freqs.withIndex()) {
                        val amp = 0.22 / (idx + 1)
                        sampleL += amp * sin(2.0 * Math.PI * f * t)
                        sampleR += amp * sin(2.0 * Math.PI * f * 1.002 * t)
                    }
                    // Warm tape noise / sub harmonic
                    sampleL += 0.04 * sin(2.0 * Math.PI * 58.0 * t)
                    sampleR += 0.04 * sin(2.0 * Math.PI * 58.0 * t)
                }
                3 -> { // Subterranean Pulse
                    val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * 2.0 * t) // 120 bpm pulse
                    sampleL = 0.45 * sin(2.0 * Math.PI * freqs[0] * t) * lfo
                    sampleR = 0.45 * sin(2.0 * Math.PI * freqs[0] * t) * lfo
                    sampleL += 0.15 * sin(2.0 * Math.PI * freqs[1] * t)
                    sampleR += 0.15 * sin(2.0 * Math.PI * freqs[2] * t)
                }
                4 -> { // Glass Rain
                    for ((idx, f) in freqs.withIndex()) {
                        val decay = ((t * (idx + 1) * 1.5) % 1.0)
                        val noteEnv = (1.0 - decay).coerceAtLeast(0.0)
                        val amp = 0.15 * noteEnv
                        sampleL += amp * sin(2.0 * Math.PI * f * t)
                        sampleR += amp * sin(2.0 * Math.PI * f * 1.004 * t)
                    }
                }
                else -> { // Velocity Zero
                    val arpStep = ((t * 4.0).toInt()) % freqs.size
                    val f = freqs[arpStep]
                    val saw = (2.0 * ((t * f) - kotlin.math.floor((t * f) + 0.5))) * 0.25
                    sampleL = saw * (1.0 + 0.3 * sin(2.0 * Math.PI * 0.5 * t))
                    sampleR = saw * (1.0 - 0.3 * sin(2.0 * Math.PI * 0.5 * t))
                }
            }

            val finalL = (sampleL * envelope).coerceIn(-0.95, 0.95)
            val finalR = (sampleR * envelope).coerceIn(-0.95, 0.95)

            val shortL = (finalL * 32767.0).toInt().toShort()
            val shortR = (finalR * 32767.0).toInt().toShort()

            buffer.putShort(shortL)
            buffer.putShort(shortR)
        }

        FileOutputStream(file).use { fos ->
            writeWavHeader(fos, numChannels, sampleRate, bytesPerSample * 8, pcmData.size)
            fos.write(pcmData)
        }
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        channels: Int,
        sampleRate: Int,
        bitsPerSample: Int,
        audioDataSize: Int
    ) {
        val totalDataLen = audioDataSize + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1Size for PCM
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // AudioFormat 1 = PCM
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = blockAlign.toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (audioDataSize and 0xff).toByte()
        header[41] = (audioDataSize shr 8 and 0xff).toByte()
        header[42] = (audioDataSize shr 16 and 0xff).toByte()
        header[43] = (audioDataSize shr 24 and 0xff).toByte()

        out.write(header, 0, 44)
    }
}
