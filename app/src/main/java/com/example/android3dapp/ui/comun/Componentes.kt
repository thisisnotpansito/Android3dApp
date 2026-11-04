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
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
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

/** Pastilla de color con el estado del modelo. */
@Composable
fun EtiquetaEstado(estado: EstadoModelo, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        EstadoModelo.LISTO -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        EstadoModelo.PROCESANDO -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        EstadoModelo.ERROR -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        EstadoModelo.BORRADOR -> Color(0xFFE5E7EB) to Color(0xFF374151)
    }
    Surface(color = fondo, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Text(
            estado.etiqueta(),
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
