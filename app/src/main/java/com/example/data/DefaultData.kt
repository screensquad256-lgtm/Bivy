package com.example.data

import com.example.model.AudioDeviceType
import com.example.model.AudioPreset
import com.example.model.AudioProfile
import com.example.model.Song

object DefaultData {
    val SYSTEM_PRESETS = listOf(
        AudioPreset(
            id = "preset_flat",
            name = "Reference Flat",
            isSystem = true,
            bandLevelsString = "0,0,0,0,0,0,0,0,0,0",
            bassBoostStrength = 0,
            virtualizerStrength = 0,
            reverbPreset = "NONE",
            trebleGainDb = 0,
            loudnessGainMb = 0,
            stereoWidth = 50
        ),
        AudioPreset(
            id = "preset_studio_master",
            name = "Studio Master",
            isSystem = true,
            bandLevelsString = "4,3,2,0,1,2,3,4,4,3",
            bassBoostStrength = 320,
            virtualizerStrength = 380,
            reverbPreset = "SMALL_ROOM",
            trebleGainDb = 2,
            loudnessGainMb = 350,
            stereoWidth = 70
        ),
        AudioPreset(
            id = "preset_bass_surge",
            name = "Bass Surge",
            isSystem = true,
            bandLevelsString = "8,7,5,2,0,0,1,2,2,1",
            bassBoostStrength = 750,
            virtualizerStrength = 250,
            reverbPreset = "NONE",
            trebleGainDb = 0,
            loudnessGainMb = 400,
            stereoWidth = 55
        ),
        AudioPreset(
            id = "preset_acoustic_warmth",
            name = "Acoustic Warmth",
            isSystem = true,
            bandLevelsString = "2,3,4,3,2,3,4,3,1,0",
            bassBoostStrength = 200,
            virtualizerStrength = 400,
            reverbPreset = "MEDIUM_ROOM",
            trebleGainDb = 1,
            loudnessGainMb = 200,
            stereoWidth = 65
        ),
        AudioPreset(
            id = "preset_vocal_clarity",
            name = "Vocal Clarity",
            isSystem = true,
            bandLevelsString = "-2,-1,1,3,5,6,5,4,2,1",
            bassBoostStrength = 100,
            virtualizerStrength = 300,
            reverbPreset = "SMALL_ROOM",
            trebleGainDb = 4,
            loudnessGainMb = 300,
            stereoWidth = 60
        ),
        AudioPreset(
            id = "preset_space_ambient",
            name = "Space Ambient",
            isSystem = true,
            bandLevelsString = "3,2,1,0,1,2,3,5,6,7",
            bassBoostStrength = 400,
            virtualizerStrength = 850,
            reverbPreset = "LARGE_HALL",
            trebleGainDb = 5,
            loudnessGainMb = 450,
            stereoWidth = 95
        ),
        AudioPreset(
            id = "preset_vinyl_70s",
            name = "Vinyl 1970s",
            isSystem = true,
            bandLevelsString = "5,4,3,2,1,0,-1,-2,-3,-4",
            bassBoostStrength = 450,
            virtualizerStrength = 150,
            reverbPreset = "MEDIUM_ROOM",
            trebleGainDb = -2,
            loudnessGainMb = 150,
            stereoWidth = 45
        ),
        AudioPreset(
            id = "preset_club_pulse",
            name = "Electronic Pulse",
            isSystem = true,
            bandLevelsString = "7,6,3,0,-2,1,4,6,7,8",
            bassBoostStrength = 650,
            virtualizerStrength = 600,
            reverbPreset = "PLATE",
            trebleGainDb = 4,
            loudnessGainMb = 500,
            stereoWidth = 80
        )
    )

    val DEFAULT_PROFILES = listOf(
        AudioProfile(
            deviceType = AudioDeviceType.HEADPHONES.name,
            presetId = "preset_studio_master",
            customName = "Studio Reference Headset",
            autoActivate = true
        ),
        AudioProfile(
            deviceType = AudioDeviceType.EARBUDS.name,
            presetId = "preset_bass_surge",
            customName = "Everyday True Wireless",
            autoActivate = true
        ),
        AudioProfile(
            deviceType = AudioDeviceType.BLUETOOTH_SPEAKER.name,
            presetId = "preset_club_pulse",
            customName = "Living Room Acoustics",
            autoActivate = true
        ),
        AudioProfile(
            deviceType = AudioDeviceType.CAR.name,
            presetId = "preset_vocal_clarity",
            customName = "Cabin Road Tuned",
            autoActivate = true
        ),
        AudioProfile(
            deviceType = AudioDeviceType.PHONE_SPEAKER.name,
            presetId = "preset_vocal_clarity",
            customName = "Handheld Tuned",
            autoActivate = true
        ),
        AudioProfile(
            deviceType = AudioDeviceType.CUSTOM.name,
            presetId = "preset_space_ambient",
            customName = "Audiophile DAC Rig",
            autoActivate = false
        )
    )

    val DEMO_STUDIO_TRACKS = listOf(
        Song(
            id = "bivy_track_1",
            title = "Orbital Resonance",
            artist = "Solaris Architecture",
            album = "Kinetic Atmosphere Vol. I",
            durationMs = 214000L,
            dataUri = "asset://audio/orbital_resonance.wav",
            genre = "Atmospheric Ambient",
            year = 2026,
            trackNumber = 1,
            bitrateKbps = 1411,
            sampleRateHz = 96000,
            codec = "FLAC 24-bit",
            isLossless = true,
            isFavorite = true,
            playCount = 18,
            lastPlayedTimestamp = System.currentTimeMillis() - 3600000L,
            lyrics = """
                [00:08.20]Floating through the quiet frequency
                [00:15.50]Where analog light embraces clarity
                [00:24.10]Resonance in every hidden octave
                [00:32.40]The sound expands beyond the spectrum
                [00:41.00]Hear the orbit spin in gold
                [00:49.80]A personal studio in the cold
                [00:58.20]Every transient, every beat
                [01:06.50]Pure sound beneath our feet
            """.trimIndent()
        ),
        Song(
            id = "bivy_track_2",
            title = "Analog Horizon",
            artist = "Veloce Trio",
            album = "Midnight Monolith",
            durationMs = 186000L,
            dataUri = "asset://audio/analog_horizon.wav",
            genre = "Neo-Soul / Jazz",
            year = 2025,
            trackNumber = 2,
            bitrateKbps = 920,
            sampleRateHz = 48000,
            codec = "ALAC Lossless",
            isLossless = true,
            isFavorite = true,
            playCount = 29,
            lastPlayedTimestamp = System.currentTimeMillis() - 7200000L,
            lyrics = """
                [00:06.10]Warm tape saturation in the breeze
                [00:14.30]Sub-harmonic frequencies through the trees
                [00:23.00]Turn the dial up to the golden hue
                [00:31.20]Nothing comes between the sound and you
                [00:40.00]Step into the sound space
                [00:48.50]Feel the depth in every place
            """.trimIndent()
        ),
        Song(
            id = "bivy_track_3",
            title = "Subterranean Pulse",
            artist = "Kroma Dynamic",
            album = "Low-End Architecture",
            durationMs = 245000L,
            dataUri = "asset://audio/subterranean_pulse.wav",
            genre = "Deep Electronic",
            year = 2026,
            trackNumber = 3,
            bitrateKbps = 1411,
            sampleRateHz = 88200,
            codec = "WAV PCM",
            isLossless = true,
            isFavorite = false,
            playCount = 12,
            lastPlayedTimestamp = System.currentTimeMillis() - 86400000L,
            lyrics = """
                [00:12.00]30 Hertz tremors in the night
                [00:22.40]Synthesizer shadows catching light
                [00:34.00]Limiter threshold holding tight
                [00:44.20]Feel the studio take flight
            """.trimIndent()
        ),
        Song(
            id = "bivy_track_4",
            title = "Glass Rain",
            artist = "Elysian Strings",
            album = "Acoustic Elements",
            durationMs = 312000L,
            dataUri = "asset://audio/glass_rain.wav",
            genre = "Modern Classical",
            year = 2024,
            trackNumber = 4,
            bitrateKbps = 320,
            sampleRateHz = 44100,
            codec = "FLAC 16-bit",
            isLossless = true,
            isFavorite = true,
            playCount = 7,
            lastPlayedTimestamp = System.currentTimeMillis() - 259200000L,
            lyrics = """
                [00:10.00]Drops upon the studio roof
                [00:20.50]Purity in silence is the proof
                [00:31.00]Gentle reverb in the hall
                [00:42.00]Echoes answering the call
            """.trimIndent()
        ),
        Song(
            id = "bivy_track_5",
            title = "Velocity Zero",
            artist = "Astral Drift",
            album = "Zero Gravity Sessions",
            durationMs = 198000L,
            dataUri = "asset://audio/velocity_zero.wav",
            genre = "Synthwave / Cyber",
            year = 2026,
            trackNumber = 5,
            bitrateKbps = 960,
            sampleRateHz = 48000,
            codec = "OPUS Hi-Res",
            isLossless = false,
            isFavorite = false,
            playCount = 15,
            lastPlayedTimestamp = System.currentTimeMillis() - 172800000L,
            lyrics = """
                [00:09.00]Cruising at the neon border
                [00:18.00]Sound escaping every order
                [00:27.50]Virtualizer spreading wide
                [00:36.00]Stereo field on every side
            """.trimIndent()
        )
    )
}
