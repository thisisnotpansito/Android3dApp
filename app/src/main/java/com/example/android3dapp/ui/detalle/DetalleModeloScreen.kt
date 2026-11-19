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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text("Detalle", fontWeight = FontWeight.SemiBold) },
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
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Banner Visual Atractivo
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(
                    when {
                        modelo.tieneArchivo3D -> "Modelo 3D listo\nTócalo en «Girar en 360°»"
                        modelo.estado == EstadoModelo.ERROR -> "La generación falló"
                        modelo.estado == EstadoModelo.PROCESANDO -> "Generando el modelo 3D…"
                        else -> "Modelo de ejemplo\nsin archivo 3D"
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        // Título y Estado
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(modelo.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            EtiquetaEstado(modelo.estado)
        }

        // Tarjeta de Detalles Técnicos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Información", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Dato("Creado el", formatearFecha(modelo.creadoEnMillis))
                Dato("Detalle geométrico", formatearPoligonos(modelo.poligonos))
                Dato("Identificador", modelo.id)
                if (modelo.mensajeError != null) {
                    Spacer(Modifier.height(4.dp))
                    Dato("Motivo del error", modelo.mensajeError, isError = true)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Acciones Principales
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = alVer360, 
                enabled = modelo.tieneArchivo3D, 
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (modelo.tieneArchivo3D) "Girar en 360°" else "No disponible para girar", fontWeight = FontWeight.Bold)
            }
            
            OutlinedButton(
                onClick = {}, 
                enabled = false, 
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ver en AR (Próximamente)")
            }

            TextButton(
                onClick = { confirmar = true },
                enabled = !e.borrando,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) { 
                Text(if (e.borrando) "Borrando…" else "Borrar permanentemente") 
            }
        }
        
        if (e.errorAlBorrar) {
            Text("No se pudo borrar. Los modelos de ejemplo no se borran; si es tuyo, revisa tu conexión.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Eliminar modelo?") },
            text = { Text("Se borrará «${modelo.nombre}» y su archivo 3D. Esta acción no se puede deshacer.") },
            confirmButton = { 
                Button(onClick = { confirmar = false; alBorrar() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Eliminar") } 
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun Dato(nombre: String, valor: String, isError: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(nombre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.weight(1.5f))
    }
}
