package com.example

import android.app.Application
import com.example.audio.StudioAudioSynthesizer
import com.example.data.BivyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BivyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var repository: BivyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = BivyRepository(this)

        applicationScope.launch {
            // Synthesize pristine studio demonstration audio files for instant playback
            StudioAudioSynthesizer.prepareDemoAudioFiles(this@BivyApplication)
            repository.initializeDefaultsIfNeeded()
        }
    }
}
