package barbieri.claudio.galeria

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import barbieri.claudio.galeria.ui.theme.GaleriaTheme
import coil.compose.rememberAsyncImagePainter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
class PhotoActivity : ComponentActivity() {

    val photoViewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoViewModel.setImage(getExtraFile())
        enableEdgeToEdge()
        setContent {
            GaleriaTheme {
                Screen()
            }
        }
    }

    @Composable
    private fun Screen() {
        val image = photoViewModel.image.collectAsStateWithLifecycle().value

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text("Galeria", color = Color.White) },
                    navigationIcon = {
                        IconButton(
                            onClick = { this@PhotoActivity.onBackPressedDispatcher.onBackPressed() },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back icon",
                                    tint = Color.White
                                )
                            }
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_STREAM, image)
                                    type = "image/jpeg"
                                }
                                startActivity(intent)
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share icon",
                                    tint = Color.White
                                )
                            }
                        )
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentScale = ContentScale.FillBounds,
                painter = rememberAsyncImagePainter(image),
                contentDescription = "Image"
            )
        }
    }

    private fun getExtraFile(): File? {
        return intent.getSerializableExtra("image") as? File
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        GaleriaTheme {
            Screen()
        }
    }
}