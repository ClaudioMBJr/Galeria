package barbieri.claudio.galeria

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class MainViewModel : ViewModel() {

    val myImages = MutableStateFlow<MutableList<File>>(mutableStateListOf())

    private var file: File? = null

    fun addImage() {
        file?.let {
            myImages.value.add(it)
        }
    }

    fun setTempFile(file: File) {
        this.file = file
    }

    fun getTempFile() = file
}
