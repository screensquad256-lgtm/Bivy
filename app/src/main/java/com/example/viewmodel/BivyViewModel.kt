package com.example.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BivyApplication
import com.example.audio.ABMode
import com.example.audio.BivyAudioEngine
import com.example.audio.StudioAudioSynthesizer
import com.example.audio.VisualizerSnapshot
import com.example.data.DefaultData
import com.example.model.AudioDeviceType
import com.example.model.AudioPreset
import com.example.model.AudioProfile
import com.example.model.PlaybackMode
import com.example.model.Playlist
import com.example.model.Song
import com.example.service.BivyPlaybackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    AUDIO_SPACE,
    NOW_PLAYING,
    AUDIO_LAB,
    VIBEMIX,
    MY_SOUND,
    CAR_MODE,
    LIBRARY_BROWSE,
    PRIVACY_CENTER,
    PRO_STORE
}

enum class OrbitRing(val label: String) {
    RECENT("Recent"),
    FAVORITES("Favorites"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists"),
    GENRES("Genres"),
    MOODS("VibeMix"),
    STUDIO_FX("Audio Lab")
}

enum class LibraryFilter(val displayName: String) {
    ALL("All Songs"),
    FAVORITES("Favorites"),
    RECENT("Recently Added"),
    MOST_PLAYED("Most Played"),
    LOSSLESS("Hi-Res / Lossless"),
    FORGOTTEN("Forgotten (30+ Days)")
}

class BivyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BivyApplication).repository
    private val audioEngine = BivyAudioEngine(application, viewModelScope)

    // Bound playback service
    private var playbackService: BivyPlaybackService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playbackService = (service as? BivyPlaybackService.LocalBinder)?.getService()
            playbackService?.updateNotification(audioEngine.currentSong.value, audioEngine.isPlaying.value)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            playbackService = null
        }
    }

    // Navigation & UI States
    private val _currentScreen = MutableStateFlow(Screen.AUDIO_SPACE)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<Screen>()

    private val _selectedOrbitRing = MutableStateFlow(OrbitRing.RECENT)
    val selectedOrbitRing: StateFlow<OrbitRing> = _selectedOrbitRing.asStateFlow()

    private val _activeFilter = MutableStateFlow(LibraryFilter.ALL)
    val activeFilter: StateFlow<LibraryFilter> = _activeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Subscription & Pro Features
    private val _isProSubscriber = MutableStateFlow(true) // Default to unlocked Pro Studio for user evaluation
    val isProSubscriber: StateFlow<Boolean> = _isProSubscriber.asStateFlow()

    // Voice Control
    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _voiceFeedback = MutableStateFlow<String?>(null)
    val voiceFeedback: StateFlow<String?> = _voiceFeedback.asStateFlow()

    // Queue & Playback Modes
    private val _activeQueue = MutableStateFlow<List<Song>>(emptyList())
    val activeQueue: StateFlow<List<Song>> = _activeQueue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    private val _playbackMode = MutableStateFlow(PlaybackMode.REPEAT_ALL)
    val playbackMode: StateFlow<PlaybackMode> = _playbackMode.asStateFlow()

    // Library Data Flows
    val allSongs: StateFlow<List<Song>> = repository.allSongs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DefaultData.DEMO_STUDIO_TRACKS
    )

    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val recentlyPlayed: StateFlow<List<Song>> = repository.recentlyPlayed.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val mostPlayed: StateFlow<List<Song>> = repository.mostPlayed.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val losslessSongs: StateFlow<List<Song>> = repository.losslessSongs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val forgottenFavorites: StateFlow<List<Song>> = repository.forgottenFavorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allPresets: StateFlow<List<AudioPreset>> = repository.allPresets.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DefaultData.SYSTEM_PRESETS
    )

    val allProfiles: StateFlow<List<AudioProfile>> = repository.allProfiles.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DefaultData.DEFAULT_PROFILES
    )

    val allPlaylists: StateFlow<List<Playlist>> = repository.allPlaylists.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val totalListeningTimeMs: StateFlow<Long?> = repository.totalListeningTimeMs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0L
    )

    // Audio Engine Proxy States
    val currentSong: StateFlow<Song?> = audioEngine.currentSong
    val isPlaying: StateFlow<Boolean> = audioEngine.isPlaying
    val currentPositionMs: StateFlow<Long> = audioEngine.currentPositionMs
    val durationMs: StateFlow<Long> = audioEngine.durationMs
    val currentPreset: StateFlow<AudioPreset?> = audioEngine.currentPreset
    val abMode: StateFlow<ABMode> = audioEngine.abMode
    val visualizerSnapshot: StateFlow<VisualizerSnapshot> = audioEngine.visualizerSnapshot
    val currentDeviceType: StateFlow<AudioDeviceType> = audioEngine.currentDeviceType

    // Filtered / Search list
    val displayedSongs: StateFlow<List<Song>> = combine(
        allSongs,
        activeFilter,
        searchQuery
    ) { all, filter, query ->
        val cutoff = System.currentTimeMillis() - 30L * 24 * 3600 * 1000
        val baseList = when (filter) {
            LibraryFilter.ALL -> all
            LibraryFilter.FAVORITES -> all.filter { it.isFavorite }
            LibraryFilter.RECENT -> all.filter { it.lastPlayedTimestamp > 0 }.sortedByDescending { it.lastPlayedTimestamp }
            LibraryFilter.MOST_PLAYED -> all.sortedByDescending { it.playCount }
            LibraryFilter.LOSSLESS -> all.filter { it.isLossless }
            LibraryFilter.FORGOTTEN -> all.filter { it.isFavorite && it.lastPlayedTimestamp < cutoff }
        }
        if (query.isBlank()) {
            baseList
        } else {
            val q = query.trim().lowercase()
            baseList.filter {
                it.title.lowercase().contains(q) ||
                        it.artist.lowercase().contains(q) ||
                        it.album.lowercase().contains(q) ||
                        it.genre.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Bind foreground playback service
        val intent = Intent(application, BivyPlaybackService::class.java)
        application.startService(intent)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Set default studio master preset
        audioEngine.setAudioPreset(DefaultData.SYSTEM_PRESETS[1])

        // Audio completion listener
        audioEngine.setOnSongCompletedListener {
            viewModelScope.launch {
                val song = audioEngine.currentSong.value
                if (song != null) {
                    repository.recordPlayback(song, audioEngine.durationMs.value)
                }
                handleTrackCompletion()
            }
        }

        // Keep service notification synced
        viewModelScope.launch {
            combine(currentSong, isPlaying) { song, playing ->
                playbackService?.updateNotification(song, playing)
            }.collect {}
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenBackStack.isNotEmpty()) {
            _currentScreen.value = _screenBackStack.removeAt(_screenBackStack.lastIndex)
            return true
        }
        if (_currentScreen.value != Screen.AUDIO_SPACE) {
            _currentScreen.value = Screen.AUDIO_SPACE
            return true
        }
        return false
    }

    fun selectOrbitRing(ring: OrbitRing) {
        _selectedOrbitRing.value = ring
        when (ring) {
            OrbitRing.RECENT -> {
                _activeFilter.value = LibraryFilter.RECENT
                navigateTo(Screen.LIBRARY_BROWSE)
            }
            OrbitRing.FAVORITES -> {
                _activeFilter.value = LibraryFilter.FAVORITES
                navigateTo(Screen.LIBRARY_BROWSE)
            }
            OrbitRing.ALBUMS, OrbitRing.ARTISTS, OrbitRing.GENRES -> {
                _activeFilter.value = LibraryFilter.ALL
                navigateTo(Screen.LIBRARY_BROWSE)
            }
            OrbitRing.PLAYLISTS -> {
                _activeFilter.value = LibraryFilter.ALL
                navigateTo(Screen.LIBRARY_BROWSE)
            }
            OrbitRing.MOODS -> {
                navigateTo(Screen.VIBEMIX)
            }
            OrbitRing.STUDIO_FX -> {
                navigateTo(Screen.AUDIO_LAB)
            }
        }
    }

    fun setActiveFilter(filter: LibraryFilter) {
        _activeFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playSong(song: Song, queue: List<Song>? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            val q = queue ?: allSongs.value
            _activeQueue.value = q
            val idx = q.indexOfFirst { it.id == song.id }
            _queueIndex.value = if (idx != -1) idx else 0

            // Ensure physical audio exists for demo tracks
            if (song.id.startsWith("bivy_track_")) {
                StudioAudioSynthesizer.prepareDemoAudioFiles(getApplication())
            }

            audioEngine.playSong(song)
        }
    }

    fun togglePlayPause() {
        if (currentSong.value == null) {
            val list = allSongs.value
            if (list.isNotEmpty()) {
                playSong(list.first())
            }
        } else {
            audioEngine.togglePlayPause()
        }
    }

    fun playNext() {
        val q = _activeQueue.value
        if (q.isEmpty()) return

        val nextIndex = when (_playbackMode.value) {
            PlaybackMode.SHUFFLE -> (q.indices).random()
            PlaybackMode.REPEAT_ONE -> _queueIndex.value
            PlaybackMode.REPEAT_ALL -> (_queueIndex.value + 1) % q.size
        }
        _queueIndex.value = nextIndex
        playSong(q[nextIndex], q)
    }

    fun playPrevious() {
        val q = _activeQueue.value
        if (q.isEmpty()) return

        val prevIndex = if (_queueIndex.value > 0) _queueIndex.value - 1 else q.size - 1
        _queueIndex.value = prevIndex
        playSong(q[prevIndex], q)
    }

    private fun handleTrackCompletion() {
        when (_playbackMode.value) {
            PlaybackMode.REPEAT_ONE -> {
                currentSong.value?.let { audioEngine.playSong(it) }
            }
            PlaybackMode.REPEAT_ALL, PlaybackMode.SHUFFLE -> {
                playNext()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        audioEngine.seekTo(positionMs)
    }

    fun togglePlaybackMode() {
        _playbackMode.value = when (_playbackMode.value) {
            PlaybackMode.REPEAT_ALL -> PlaybackMode.REPEAT_ONE
            PlaybackMode.REPEAT_ONE -> PlaybackMode.SHUFFLE
            PlaybackMode.SHUFFLE -> PlaybackMode.REPEAT_ALL
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song)
        }
    }

    fun toggleABMode() {
        audioEngine.toggleABMode()
    }

    fun setABMode(mode: ABMode) {
        audioEngine.setABMode(mode)
    }

    fun setAudioPreset(preset: AudioPreset) {
        audioEngine.setAudioPreset(preset)
    }

    fun updateCurrentPreset(updated: AudioPreset) {
        audioEngine.setAudioPreset(updated)
    }

    fun saveCustomPreset(presetName: String) {
        val current = audioEngine.currentPreset.value ?: return
        val custom = current.copy(
            id = "preset_custom_${System.currentTimeMillis()}",
            name = presetName,
            isSystem = false
        )
        viewModelScope.launch {
            repository.savePreset(custom)
            audioEngine.setAudioPreset(custom)
        }
    }

    fun deletePreset(id: String) {
        viewModelScope.launch {
            repository.deletePreset(id)
            audioEngine.setAudioPreset(DefaultData.SYSTEM_PRESETS[0])
        }
    }

    fun setProfileForDevice(deviceType: AudioDeviceType, presetId: String) {
        val profile = AudioProfile(
            deviceType = deviceType.name,
            presetId = presetId,
            customName = deviceType.displayName,
            autoActivate = true
        )
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun triggerDeviceAutoProfile(deviceType: AudioDeviceType) {
        viewModelScope.launch {
            val profile = repository.getProfileForDevice(deviceType)
            if (profile != null && profile.autoActivate) {
                val preset = allPresets.value.find { it.id == profile.presetId }
                if (preset != null) {
                    audioEngine.setAudioPreset(preset)
                }
            }
        }
    }

    fun scanDeviceMusic() {
        viewModelScope.launch {
            repository.scanMedia()
        }
    }

    fun generateVibeMix(mood: String, activity: String, energy: String) {
        viewModelScope.launch {
            val (playlist, songs) = repository.generateVibeMix(mood, activity, energy)
            if (songs.isNotEmpty()) {
                val playlistId = repository.createPlaylist(
                    name = playlist.name,
                    description = playlist.description,
                    songs = songs,
                    isVibeMix = true,
                    vibeTag = playlist.vibeTag
                )
                playSong(songs.first(), songs)
            }
        }
    }

    fun processVoiceCommand(input: String) {
        val cmd = input.trim().lowercase()
        _voiceFeedback.value = "Executing: \"$input\""
        when {
            cmd.contains("play") && !cmd.contains("workout") && !cmd.contains("favorite") -> {
                audioEngine.resume()
                _voiceFeedback.value = "Resuming playback"
            }
            cmd.contains("pause") || cmd.contains("stop") -> {
                audioEngine.pause()
                _voiceFeedback.value = "Playback paused"
            }
            cmd.contains("next") || cmd.contains("skip") -> {
                playNext()
                _voiceFeedback.value = "Skipping to next track"
            }
            cmd.contains("previous") || cmd.contains("back") -> {
                playPrevious()
                _voiceFeedback.value = "Playing previous track"
            }
            cmd.contains("bass up") || cmd.contains("more bass") -> {
                val current = audioEngine.currentPreset.value ?: DefaultData.SYSTEM_PRESETS[1]
                val boosted = current.copy(bassBoostStrength = (current.bassBoostStrength + 250).coerceAtMost(1000))
                audioEngine.setAudioPreset(boosted)
                _voiceFeedback.value = "Bass Boost increased to ${boosted.bassBoostStrength / 10}%"
            }
            cmd.contains("rock") -> {
                val rock = allPresets.value.find { it.id == "preset_club_pulse" } ?: DefaultData.SYSTEM_PRESETS[7]
                audioEngine.setAudioPreset(rock)
                _voiceFeedback.value = "Activated ${rock.name} preset"
            }
            cmd.contains("flat") || cmd.contains("reverb off") -> {
                val flat = allPresets.value.find { it.id == "preset_flat" } ?: DefaultData.SYSTEM_PRESETS[0]
                audioEngine.setAudioPreset(flat)
                _voiceFeedback.value = "Reverb turned off, Reference Flat engaged"
            }
            cmd.contains("vibemix") || cmd.contains("workout") -> {
                generateVibeMix("Energetic", "Workout", "High")
                _voiceFeedback.value = "Generated High-Energy Workout VibeMix"
            }
            cmd.contains("favorite") -> {
                val favs = favoriteSongs.value
                if (favs.isNotEmpty()) {
                    playSong(favs.first(), favs)
                    _voiceFeedback.value = "Playing your Favorites"
                } else {
                    _voiceFeedback.value = "No favorites marked yet"
                }
            }
            else -> {
                _voiceFeedback.value = "Command recognized: \"$input\""
            }
        }
    }

    fun toggleVoiceListening() {
        _isVoiceListening.value = !_isVoiceListening.value
        if (!_isVoiceListening.value) {
            _voiceFeedback.value = null
        }
    }

    fun toggleProSubscriber() {
        _isProSubscriber.value = !_isProSubscriber.value
    }

    fun removeQueueItem(index: Int) {
        val current = _activeQueue.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _activeQueue.value = current
            if (_queueIndex.value >= current.size) {
                _queueIndex.value = (current.size - 1).coerceAtLeast(0)
            }
        }
    }

    fun shuffleQueue() {
        val current = _activeQueue.value.toMutableList()
        val currentTrack = current.getOrNull(_queueIndex.value)
        current.shuffle()
        if (currentTrack != null) {
            current.remove(currentTrack)
            current.add(0, currentTrack)
            _queueIndex.value = 0
        }
        _activeQueue.value = current
    }

    fun clearQueue() {
        _activeQueue.value = emptyList()
        _queueIndex.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<BivyApplication>().unbindService(serviceConnection)
        } catch (ignored: Exception) {}
        audioEngine.release()
    }
}
