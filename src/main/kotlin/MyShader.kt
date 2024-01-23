import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.window.WindowState
import org.intellij.lang.annotations.Language
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

@Composable
fun myShader(windowState: WindowState) {

    // Define the shader code as a string
    @Language("GLSL")
    val sksl = """
            uniform float time;
            uniform float iResolution;
            
            float f(vec3 p) {
                p.z -= 10. + time;
                float a = p.z * .1;
                p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
                return .1 - length(cos(p.xy) + sin(p.yz));
            }
            
            half4 main(vec2 fragcoord) { 
                vec3 d = .5 - fragcoord.xy1 / iResolution;
                vec3 p=vec3(0);
                for (int i = 0; i < 32; i++) p += f(p) * d;
                return ((sin(p) + vec3(2, 5, 9)) / length(p)).xyz1;
            }
        """

    // Get the height and width of the window
    val myHeight = windowState.size.height.value
    val myWidth = windowState.size.width.value

    // Create a RuntimeEffect from the shader code
    val runtimeEffect = RuntimeEffect.makeForShader(sksl)

    // Remember the current time in nanoseconds
    val startNanos = remember { System.nanoTime() }

    // Compute the elapsed time since the start
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameNanos {
                value = (startNanos - it) / 100000000f
            }
        }
    }
    // Create a RuntimeShaderBuilder from the RuntimeEffect
    // Remember the builder, so it's only created once and reused across recompositions
    val shaderBuilder = remember { RuntimeShaderBuilder(runtimeEffect) }

    // Set the uniforms for the shader
    shaderBuilder.uniform("time", time)
    shaderBuilder.uniform("iResolution", myWidth)

    // Build the shader from the builder
    val shader = shaderBuilder.makeShader()

    // Create a ShaderBrush from the shader
    val brush = ShaderBrush(shader)

    // Draw a box filled with the shader
    Box(modifier = Modifier.fillMaxSize().drawBehind {
        drawRect(
            brush = brush, topLeft = Offset(0f, 0f), size = Size(myWidth, myHeight)
        )
    })
}