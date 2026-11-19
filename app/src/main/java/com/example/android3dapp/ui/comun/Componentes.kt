package com.example.android3dapp.ui.comun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.android3dapp.domain.EstadoModelo

/** Mensaje de pantalla completa para los estados «vacío» y «error»: qué pasó y qué hacer. */
@Composable
fun Aviso(
    titulo: String,
    detalle: String,
    accion: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
        Text(
            detalle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp).widthIn(max = 320.dp),
        )
        Button(onClick = alPulsar) { Text(accion) }
    }
}

/** Pastilla de color con el estado del modelo usando estrictamente los 3 tonos de la marca. */
@Composable
fun EtiquetaEstado(estado: EstadoModelo, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        // Tono secundario (Teal) para el éxito
        EstadoModelo.LISTO -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.secondary
        // Tono acento (Ámbar) para procesamiento
        EstadoModelo.PROCESANDO -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.tertiary
        // Tono principal (Índigo) para borradores
        EstadoModelo.BORRADOR -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.primary
        // Usamos el error nativo del tema para problemas sin ensuciar la paleta base
        EstadoModelo.ERROR -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
    }
    Surface(color = fondo, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Text(
            estado.etiqueta(),
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
