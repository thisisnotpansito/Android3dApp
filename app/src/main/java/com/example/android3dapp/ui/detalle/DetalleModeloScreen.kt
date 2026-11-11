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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.comun.Aviso
import com.example.android3dapp.ui.comun.EtiquetaEstado
import com.example.android3dapp.ui.comun.formatearFecha
import com.example.android3dapp.ui.comun.formatearPoligonos

/** HU-04 · Detalle de un modelo, abierto por su id. HU-01 · desde aquí se gira en 360°. HU-10 · se borra. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleModeloScreen(vm: DetalleModeloViewModel, alVolver: () -> Unit, alVer360: (String) -> Unit) {
    val estado by vm.estado.collectAsState()
    LaunchedEffect(estado) { if (estado == DetalleUiState.Borrado) alVolver() }
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
                DetalleUiState.Cargando, DetalleUiState.Borrado -> CircularProgressIndicator()
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
                is DetalleUiState.Listo -> ContenidoDetalle(e, alVer360 = { alVer360(e.modelo.id) }, alBorrar = vm::borrar)
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(e: DetalleUiState.Listo, alVer360: () -> Unit, alBorrar: () -> Unit) {
    val modelo: Modelo3D = e.modelo
    var confirmar by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiaryContainer))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                when {
                    modelo.tieneArchivo3D -> "Modelo 3D listo\nTócalo en «Girar en 360°»"
                    modelo.estado == EstadoModelo.ERROR -> "La generación falló"
                    modelo.estado == EstadoModelo.PROCESANDO -> "Generando el modelo 3D…"
                    else -> "Modelo de ejemplo\nsin archivo 3D"
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modelo.nombre, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            EtiquetaEstado(modelo.estado)
        }
        Dato("Creado", formatearFecha(modelo.creadoEnMillis))
        Dato("Malla", formatearPoligonos(modelo.poligonos))
        Dato("Identificador", modelo.id)
        modelo.mensajeError?.let { Dato("Motivo del error", it) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = alVer360, enabled = modelo.tieneArchivo3D, modifier = Modifier.fillMaxWidth()) {
            Text(if (modelo.tieneArchivo3D) "Girar en 360°" else "Girar en 360°  ·  sin archivo 3D")
        }
        OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
            Text("Ver en tu espacio (AR)  ·  entregable 3")
        }
        TextButton(
            onClick = { confirmar = true },
            enabled = !e.borrando,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (e.borrando) "Borrando…" else "Borrar modelo") }
        if (e.errorAlBorrar) {
            Text("No se pudo borrar. Los modelos de ejemplo no se borran; si es tuyo, revisa tu conexión.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Borrar «${modelo.nombre}»?") },
            text = { Text("Se borran el modelo 3D y su foto. No se puede deshacer.") },
            confirmButton = { TextButton(onClick = { confirmar = false; alBorrar() }) { Text("Borrar") } },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun Dato(nombre: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(nombre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(1.4f))
    }
}
