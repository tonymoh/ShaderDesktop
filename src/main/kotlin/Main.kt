import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {

    val windowState = rememberWindowState(position = WindowPosition(Alignment.Center))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Desktop Shader",
        state = windowState
    ) {
        myShader(windowState)
    }
}
