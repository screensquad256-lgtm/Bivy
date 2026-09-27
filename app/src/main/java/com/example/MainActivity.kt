package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VoiceControlDialog
import com.example.ui.screens.AudioLabScreen
import com.example.ui.screens.AudioSpaceScreen
import com.example.ui.screens.CarModeScreen
import com.example.ui.screens.LibraryBrowseScreen
import com.example.ui.screens.MySoundScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PrivacyCenterScreen
import com.example.ui.screens.ProStoreScreen
import com.example.ui.screens.VibeMixScreen
import com.example.ui.theme.BivyTheme
import com.example.viewmodel.BivyViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: BivyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BivyTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val isVoiceListening by viewModel.isVoiceListening.collectAsStateWithLifecycle()

                // Permission Request Handling
                val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val audioGranted = permissions[audioPermission] ?: false
                    if (audioGranted) {
                        viewModel.scanDeviceMusic()
                    }
                }

                LaunchedEffect(Unit) {
                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        audioPermission
                    ) == PackageManager.PERMISSION_GRANTED

                    val hasRecordAudioPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    val permissionsToRequest = mutableListOf<String>()
                    if (!hasAudioPermission) permissionsToRequest.add(audioPermission)
                    if (!hasRecordAudioPermission) permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasNotificationPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasNotificationPermission) permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    if (permissionsToRequest.isNotEmpty()) {
                        permissionLauncher.launch(permissionsToRequest.toTypedArray())
                    } else {
                        viewModel.scanDeviceMusic()
                    }
                }

                // Screen Switching with Crossfade Animation
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        Screen.AUDIO_SPACE -> AudioSpaceScreen(viewModel = viewModel)
                        Screen.NOW_PLAYING -> NowPlayingScreen(viewModel = viewModel)
                        Screen.AUDIO_LAB -> AudioLabScreen(viewModel = viewModel)
                        Screen.VIBEMIX -> VibeMixScreen(viewModel = viewModel)
                        Screen.MY_SOUND -> MySoundScreen(viewModel = viewModel)
                        Screen.CAR_MODE -> CarModeScreen(viewModel = viewModel)
                        Screen.LIBRARY_BROWSE -> LibraryBrowseScreen(viewModel = viewModel)
                        Screen.PRIVACY_CENTER -> PrivacyCenterScreen(viewModel = viewModel)
                        Screen.PRO_STORE -> ProStoreScreen(viewModel = viewModel)
                    }
                }

                // Voice Control Modal Dialog
                VoiceControlDialog(
                    isOpen = isVoiceListening,
                    onDismiss = { viewModel.toggleVoiceListening() },
                    onCommand = { cmd -> viewModel.processVoiceCommand(cmd) }
                )
            }
        }
    }
}
