package com.alejandro.musicplayer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MusicPlayerScreen() }
    }
}

@Composable
fun MusicPlayerScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    var selectedName by remember { mutableStateOf("Ninguna canción seleccionada") }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedName = it.lastPathSegment?.substringAfterLast('/') ?: "Canción seleccionada"
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
            isPlaying = true
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🎵", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(20.dp))
                Text("Mi Música", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(selectedName, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { player.seekToPreviousMediaItem() }) {
                        Text("⏮", style = MaterialTheme.typography.headlineMedium)
                    }
                    Button(onClick = {
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.play()
                            isPlaying = true
                        }
                    }) {
                        Text(if (isPlaying) "Pausa" else "Reproducir")
                    }
                    IconButton(onClick = { player.seekToNextMediaItem() }) {
                        Text("⏭", style = MaterialTheme.typography.headlineMedium)
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    picker.launch(arrayOf("audio/*"))
                }) {
                    Text("Elegir canción")
                }
            }
        }
    }
}
