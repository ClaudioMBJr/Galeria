package barbieri.claudio.galeria

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class PhotoViewModel : ViewModel() {

    val image = MutableStateFlow<File?>(null)

    fun setImage(image: File?) {
        this.image.value = image
    }
}