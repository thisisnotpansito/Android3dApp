package com.example.android3dapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Forzamos modo oscuro para la estética de 3D Studio
private val StudioColorScheme = darkColorScheme(
    primary = AcentoPrincipal,
    onPrimary = Color.Black,
    secondary = AcentoSecundario,
    onSecondary = Color.White,
    tertiary = Exito,
    onTertiary = Color.White,
    background = GrafitoProfundo,
    onBackground = TextoPrincipal,
    surface = SuperficieElevada1,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieElevada2,
    onSurfaceVariant = TextoSecundario,
    error = Peligro,
    onError = Color.White,
    errorContainer = Peligro.copy(alpha = 0.15f),
    onErrorContainer = Peligro
)

@Composable
fun Android3dAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content,
    )
}
