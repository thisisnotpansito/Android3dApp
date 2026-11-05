package com.example.android3dapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    secondary = Teal80,
    tertiary = Ambar80,
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    secondary = Teal40,
    tertiary = Ambar40,
)

/**
 * Tema de la app. Se desactiva el color dinámico de Android 12+ para que la marca
 * se vea igual en todos los teléfonos (y en las capturas de la documentación).
 */
@Composable
fun Android3dAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
