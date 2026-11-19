package com.example.android3dapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TonoPrincipal,
    onPrimary = Color.White,
    secondary = TonoSecundario,
    onSecondary = Color.White,
    tertiary = TonoAcento,
    onTertiary = Color.White,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = SuperficieClara,
    onSurface = TextoClaro,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569)
)

private val DarkColorScheme = darkColorScheme(
    primary = TonoPrincipal, // Mantenemos la identidad fuerte
    onPrimary = Color.White,
    secondary = TonoSecundario,
    onSecondary = Color.White,
    tertiary = TonoAcento,
    onTertiary = Color.White,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = SuperficieOscura,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8)
)

/**
 * Tema de la app unificado en 3 tonos de identidad.
 * Se desactiva el color dinámico de Android para asegurar consistencia visual en la marca.
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
