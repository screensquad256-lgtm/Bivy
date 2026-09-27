package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.model.AudioDeviceType
import com.example.model.AudioPreset
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sin

enum class ABMode {
    ENHANCED,
    ORIGINAL
}

data class VisualizerSnapshot(
    val waveformPoints: FloatArray = FloatArray(48) { 0.1f },
    val spectrumBands: FloatArray = FloatArray(16) { 0.15f },
    val peakLevel: Float = 0.2f
)

class BivyAudioEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val TAG = "BivyAudioEngine"
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var mediaPlayer: MediaPlayer? = null

    // Audio effects
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var presetReverb: PresetReverb? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    // State
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _currentPreset = MutableStateFlow<AudioPreset?>(null)
    val currentPreset: StateFlow<AudioPreset?> = _currentPreset.asStateFlow()

    private val _abMode = MutableStateFlow(ABMode.ENHANCED)
    val abMode: StateFlow<ABMode> = _abMode.asStateFlow()

    private val _visualizerSnapshot = MutableStateFlow(VisualizerSnapshot())
    val visualizerSnapshot: StateFlow<VisualizerSnapshot> = _visualizerSnapshot.asStateFlow()

    private val _currentDeviceType = MutableStateFlow(AudioDeviceType.PHONE_SPEAKER)
    val currentDeviceType: StateFlow<AudioDeviceType> = _currentDeviceType.asStateFlow()

    private var progressJob: Job? = null
    private var visualizerJob: Job? = null
    private var onSongCompletedListener: (() -> Unit)? = null

    // Audio Focus
    private var audioFocusRequest: AudioFocusRequest? = null
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.setVolume(0.2f, 0.2f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer?.setVolume(1.0f, 1.0f)
                resume()
            }
        }
    }

    init {
        detectCurrentAudioDevice()
        startVisualizerEngine()
    }

    fun setOnSongCompletedListener(listener: () -> Unit) {
        onSongCompletedListener = listener
    }

    fun detectCurrentAudioDevice() {
        val isBt = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
        val isHeadphones = audioManager.isWiredHeadsetOn
        val detected = when {
            isBt -> AudioDeviceType.BLUETOOTH_SPEAKER
            isHeadphones -> AudioDeviceType.HEADPHONES
            else -> AudioDeviceType.PHONE_SPEAKER
        }
        _currentDeviceType.value = detected
    }

    fun playSong(song: Song) {
        try {
            releasePlayer()
            _currentSong.value = song

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                if (song.dataUri.startsWith("content://") || song.dataUri.startsWith("file://")) {
                    setDataSource(context, Uri.parse(song.dataUri))
                } else {
                    val file = File(song.dataUri)
                    if (file.exists()) {
                        setDataSource(file.absolutePath)
                    } else {
                        // Fallback synthesized track
                        val demoFile = File(context.filesDir, "orbital_resonance.wav")
                        if (demoFile.exists()) {
                            setDataSource(demoFile.absolutePath)
                        } else {
                            Log.w(TAG, "Audio file not found: ${song.dataUri}")
                        }
                    }
                }

                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                    _durationMs.value = if (song.durationMs > 0) song.durationMs else mp.duration.toLong()
                    startProgressTracker()
                    setupAudioEffects(mp.audioSessionId)
                    applyCurrentPreset()
                }

                setOnCompletionListener {
                    _isPlaying.value = false
                    onSongCompletedListener?.invoke()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    _isPlaying.value = false
                    true
                }

                prepareAsync()
            }
            mediaPlayer = player
            requestAudioFocus()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing song: ${e.message}", e)
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            }
        }
    }

    fun resume() {
        if (requestAudioFocus()) {
            mediaPlayer?.let {
                it.start()
                _isPlaying.value = true
                startProgressTracker()
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(positionMs: Long) {
        val target = positionMs.coerceIn(0L, _durationMs.value)
        mediaPlayer?.seekTo(target.toInt())
        _currentPositionMs.value = target
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioFocusRequest = request
            audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun setupAudioEffects(sessionId: Int) {
        releaseEffects()
        try {
            equalizer = Equalizer(0, sessionId).apply { enabled = true }
        } catch (e: Exception) {
            Log.w(TAG, "Equalizer init error: ${e.message}")
        }

        try {
            bassBoost = BassBoost(0, sessionId).apply { enabled = true }
        } catch (e: Exception) {
            Log.w(TAG, "BassBoost init error: ${e.message}")
        }

        try {
            presetReverb = PresetReverb(0, sessionId).apply { enabled = true }
        } catch (e: Exception) {
            Log.w(TAG, "PresetReverb init error: ${e.message}")
        }

        try {
            virtualizer = Virtualizer(0, sessionId).apply { enabled = true }
        } catch (e: Exception) {
            Log.w(TAG, "Virtualizer init error: ${e.message}")
        }

        try {
            loudnessEnhancer = LoudnessEnhancer(sessionId).apply { enabled = true }
        } catch (e: Exception) {
            Log.w(TAG, "LoudnessEnhancer init error: ${e.message}")
        }
    }

    fun setAudioPreset(preset: AudioPreset) {
        _currentPreset.value = preset
        applyCurrentPreset()
    }

    fun toggleABMode() {
        _abMode.value = if (_abMode.value == ABMode.ENHANCED) ABMode.ORIGINAL else ABMode.ENHANCED
        applyCurrentPreset()
    }

    fun setABMode(mode: ABMode) {
        _abMode.value = mode
        applyCurrentPreset()
    }

    private fun applyCurrentPreset() {
        val preset = _currentPreset.value ?: return
        val isEnhanced = _abMode.value == ABMode.ENHANCED

        // 1. Equalizer
        equalizer?.let { eq ->
            eq.enabled = isEnhanced
            if (isEnhanced) {
                val bands = preset.getBandLevels()
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]
                val numBands = eq.numberOfBands.toInt()

                for (i in 0 until numBands) {
                    val bandDb = if (i < bands.size) bands[i] else 0
                    // Convert dB to millibels (-12dB = -1200mB)
                    val mb = (bandDb * 100).coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                    eq.setBandLevel(i.toShort(), mb)
                }
            }
        }

        // 2. Bass Boost
        bassBoost?.let { bb ->
            bb.enabled = isEnhanced && preset.bassBoostStrength > 0
            if (isEnhanced) {
                bb.setStrength(preset.bassBoostStrength.toShort().coerceIn(0, 1000))
            }
        }

        // 3. Virtualizer
        virtualizer?.let { v ->
            v.enabled = isEnhanced && preset.virtualizerStrength > 0
            if (isEnhanced) {
                v.setStrength(preset.virtualizerStrength.toShort().coerceIn(0, 1000))
            }
        }

        // 4. Reverb
        presetReverb?.let { pr ->
            pr.enabled = isEnhanced && preset.reverbPreset != "NONE"
            if (isEnhanced) {
                val reverbShort: Short = when (preset.reverbPreset) {
                    "SMALL_ROOM" -> PresetReverb.PRESET_SMALLROOM
                    "MEDIUM_ROOM" -> PresetReverb.PRESET_MEDIUMROOM
                    "LARGE_ROOM" -> PresetReverb.PRESET_LARGEROOM
                    "MEDIUM_HALL" -> PresetReverb.PRESET_MEDIUMHALL
                    "LARGE_HALL" -> PresetReverb.PRESET_LARGEHALL
                    "PLATE" -> PresetReverb.PRESET_PLATE
                    else -> PresetReverb.PRESET_NONE
                }
                pr.preset = reverbShort
            }
        }

        // 5. Loudness Enhancer
        loudnessEnhancer?.let { le ->
            le.enabled = isEnhanced && preset.loudnessGainMb > 0
            if (isEnhanced) {
                le.setTargetGain(preset.loudnessGainMb.coerceIn(0, 1000))
            }
        }

        // 6. PlaybackParams (Speed & Pitch)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { mp ->
                try {
                    val params = mp.playbackParams
                    if (isEnhanced) {
                        params.speed = preset.speedRatio.coerceIn(0.5f, 2.5f)
                        params.pitch = preset.pitchRatio.coerceIn(0.5f, 2.0f)
                    } else {
                        params.speed = 1.0f
                        params.pitch = 1.0f
                    }
                    mp.playbackParams = params
                } catch (e: Exception) {
                    Log.w(TAG, "Error setting playback params: ${e.message}")
                }
            }
        }

        // 7. Stereo Balance
        mediaPlayer?.let { mp ->
            if (isEnhanced) {
                val balance = preset.stereoBalance.coerceIn(-1.0f, 1.0f)
                val left = if (balance > 0) 1.0f - balance else 1.0f
                val right = if (balance < 0) 1.0f + balance else 1.0f
                mp.setVolume(left, right)
            } else {
                mp.setVolume(1.0f, 1.0f)
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPositionMs.value = mp.currentPosition.toLong()
                    }
                }
                delay(200)
            }
        }
    }

    private fun startVisualizerEngine() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch(Dispatchers.Default) {
            var phase = 0f
            while (isActive) {
                if (_isPlaying.value) {
                    val preset = _currentPreset.value
                    val bassFactor = if (_abMode.value == ABMode.ENHANCED && preset != null) {
                        1.0f + (preset.bassBoostStrength / 1000f) * 0.8f
                    } else 1.0f

                    val wave = FloatArray(48)
                    for (i in 0 until 48) {
                        val angle = (i * 0.3f) + phase
                        val base = (sin(angle) * 0.4f + sin(angle * 2.3f) * 0.25f + 0.5f).toFloat()
                        wave[i] = (base * bassFactor * 0.65f).coerceIn(0.05f, 1.0f)
                    }

                    val bands = FloatArray(16)
                    for (j in 0 until 16) {
                        val decay = (1.0f - (j * 0.04f))
                        val freqSin = sin((j * 0.7f) + (phase * 1.5f))
                        val mag = (0.35f + freqSin * 0.35f) * decay * bassFactor
                        bands[j] = mag.coerceIn(0.08f, 1.0f)
                    }

                    _visualizerSnapshot.value = VisualizerSnapshot(
                        waveformPoints = wave,
                        spectrumBands = bands,
                        peakLevel = (0.5f + sin(phase * 2f) * 0.3f).coerceIn(0.1f, 1.0f)
                    )
                    phase += 0.15f
                } else {
                    // Rest state
                    _visualizerSnapshot.value = VisualizerSnapshot()
                }
                delay(50)
            }
        }
    }

    private fun releaseEffects() {
        try { equalizer?.release() } catch (ignored: Exception) {}
        try { bassBoost?.release() } catch (ignored: Exception) {}
        try { presetReverb?.release() } catch (ignored: Exception) {}
        try { virtualizer?.release() } catch (ignored: Exception) {}
        try { loudnessEnhancer?.release() } catch (ignored: Exception) {}
        equalizer = null
        bassBoost = null
        presetReverb = null
        virtualizer = null
        loudnessEnhancer = null
    }

    fun release() {
        progressJob?.cancel()
        visualizerJob?.cancel()
        releaseEffects()
        releasePlayer()
    }

    private fun releasePlayer() {
        progressJob?.cancel()
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing MediaPlayer: ${e.message}")
            }
        }
        mediaPlayer = null
    }
}
