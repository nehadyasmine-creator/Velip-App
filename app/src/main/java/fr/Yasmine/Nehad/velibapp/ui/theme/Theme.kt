package fr.Yasmine.Nehad.velibapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Couleurs Vélib
val VelibGreen = Color(0xFF2E7D32)
val VelibLightGreen = Color(0xFF4CAF50)
val VelibBackground = Color(0xFFF5F5F5)
val VelibSurface = Color(0xFFFFFFFF)
val VelibAccent = Color(0xFF1565C0)

private val LightColorScheme = lightColorScheme(
    primary = VelibGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    secondary = VelibAccent,
    onSecondary = Color.White,
    background = VelibBackground,
    surface = VelibSurface,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun VelibAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}