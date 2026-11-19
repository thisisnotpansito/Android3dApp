package com.example.android3dapp.ui.comun

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.ui.theme.BordeSutil
import com.example.android3dapp.ui.theme.Exito
import com.example.android3dapp.ui.theme.Peligro
import com.example.android3dapp.ui.theme.Precaucion
import com.example.android3dapp.ui.theme.SuperficieElevada2

@Composable
fun Aviso(
    titulo: String,
    detalle: String,
    accion: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Default.ErrorOutline,
    isError: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isError) MaterialTheme.colorScheme.errorContainer else SuperficieElevada2,
            border = BorderStroke(1.dp, BordeSutil),
            modifier = Modifier.size(80.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    icono, 
                    contentDescription = null, 
                    tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
        Text(
            detalle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp).widthIn(max = 320.dp),
        )
        Button(
            onClick = alPulsar,
            shape = RoundedCornerShape(12.dp)
        ) { 
            Text(accion, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) 
        }
    }
}

@Composable
fun AvisoVacio(
    titulo: String,
    detalle: String,
    accion: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Aviso(titulo, detalle, accion, alPulsar, modifier, Icons.Default.Inbox, isError = false)
}

@Composable
fun EtiquetaEstado(estado: EstadoModelo, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        EstadoModelo.LISTO -> Exito.copy(alpha = 0.15f) to Exito
        EstadoModelo.PROCESANDO -> Precaucion.copy(alpha = 0.15f) to Precaucion
        EstadoModelo.BORRADOR -> SuperficieElevada2 to MaterialTheme.colorScheme.onSurfaceVariant
        EstadoModelo.ERROR -> Peligro.copy(alpha = 0.15f) to Peligro
    }
    Surface(
        color = fondo, 
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, texto.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            estado.etiqueta().uppercase(),
            color = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun Box(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit) {
    androidx.compose.foundation.layout.Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
}
