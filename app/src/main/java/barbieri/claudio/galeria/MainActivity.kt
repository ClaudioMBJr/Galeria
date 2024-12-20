package barbieri.claudio.galeria

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import barbieri.claudio.galeria.ui.theme.GaleriaTheme
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    val requestPermissionLauncher =
        registerForActivityResult(
            RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_LONG)
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        enableEdgeToEdge()
        setContent {
            GaleriaTheme {
                Screen()
            }
        }
    }

    @Composable
    private fun Screen() {
        val myImages = mainViewModel.myImages.collectAsStateWithLifecycle().value
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) mainViewModel.addImage()
            }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text("Galeria", color = Color.White) },
                    actions = {
                        IconButton(
                            onClick = {
                                mainViewModel.setTempFile(createImageFile())
                                mainViewModel.getTempFile()?.let { file ->
                                    val uri = FileProvider.getUriForFile(
                                        this@MainActivity,
                                        "barbieri.claudio.galeria.fileprovider",
                                        file
                                    )
                                    cameraLauncher.launch(uri)
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_camera_alt_24),
                                    contentDescription = "Camera icon",
                                    tint = Color.White
                                )
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp),
                columns = GridCells.Adaptive(120.dp),
            ) {
                items(myImages) { image ->
                    Card(
                        modifier = Modifier.size(120.dp).padding(8.dp),
                        shape = RoundedCornerShape(5.dp),
                        border = BorderStroke(2.dp, Color.White),
                        onClick = {
                            val intent = Intent(this@MainActivity, PhotoActivity::class.java)
                            intent.putExtra("image", image)
                            startActivity(intent)
                        },
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize().align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Inside,
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = "Image",
                        )
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                CAMERA_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) requestPermissionLauncher.launch(CAMERA_PERMISSION)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(TIMESTAMP, Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        GaleriaTheme {
            Screen()
        }
    }

    companion object {
        private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
        private const val TIMESTAMP = "yyyyMMdd_HHmmss"
    }
}