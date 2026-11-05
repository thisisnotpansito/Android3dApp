package com.example.android3dapp.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.comun.Aviso
import com.example.android3dapp.ui.comun.EtiquetaEstado
import com.example.android3dapp.ui.comun.formatearFecha
import com.example.android3dapp.ui.comun.formatearPoligonos

/** HU-04 · Detalle de un modelo, abierto por su id. «Volver» regresa a la lista. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleModeloScreen(vm: DetalleModeloViewModel, alVolver: () -> Unit) {
    val estado by vm.estado.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del modelo") },
                navigationIcon = { TextButton(onClick = alVolver) { Text("Volver") } },
            )
        },
    ) { relleno ->
        Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val e = estado) {
                DetalleUiState.Cargando -> CircularProgressIndicator()
                DetalleUiState.Error -> Aviso(
                    titulo = "No pudimos abrir este modelo",
                    detalle = "Algo falló al leerlo. Inténtalo otra vez.",
                    accion = "Reintentar",
                    alPulsar = vm::cargar,
                )
                DetalleUiState.NoEncontrado -> Aviso(
                    titulo = "Este modelo ya no existe",
                    detalle = "Puede que se haya borrado. Vuelve a tu lista.",
                    accion = "Volver a Mis Modelos",
                    alPulsar = alVolver,
                )
                is DetalleUiState.Listo -> ContenidoDetalle(e.modelo)
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(modelo: Modelo3D) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Lugar reservado para el visor 3D (entregable 2).
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiaryContainer),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Vista 3D\ndisponible en el entregable 2",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modelo.nombre, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            EtiquetaEstado(modelo.estado)
        }
        Dato("Creado", formatearFecha(modelo.creadoEnMillis))
        Dato("Malla", formatearPoligonos(modelo.poligonos))
        Dato("Identificador", modelo.id)
        Spacer(Modifier.height(8.dp))
        Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text("Girar en 360°  ·  entregable 2")
        }
        OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text("Ver en tu espacio (AR)  ·  entregable 3")
        }
    }
}

@Composable
private fun Dato(nombre: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(nombre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
