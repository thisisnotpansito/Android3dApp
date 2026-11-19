package com.example.android3dapp.ui.modelos

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.android3dapp.app.AppContenedor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android3dapp.data.Escenario
import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.comun.Aviso
import com.example.android3dapp.ui.comun.AvisoVacio
import com.example.android3dapp.ui.comun.EtiquetaEstado
import com.example.android3dapp.ui.comun.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisModelosScreen(
    vm: MisModelosViewModel,
    alAbrirModelo: (String) -> Unit,
    alCrear: () -> Unit,
    alVolver: () -> Unit,
) {
    val estado by vm.estado.collectAsState()
    LaunchedEffect(Unit) { vm.refrescar() }
    val esDepuracion = (LocalContext.current.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Librería 3D", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Inicio")
                    }
                },
                actions = { if (esDepuracion && vm.admiteEscenarios) SelectorEscenario(vm::elegirEscenario) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (estado is MisModelosUiState.ConDatos) {
                FloatingActionButton(
                    onClick = alCrear,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Modelo")
                }
            }
        }
    ) { relleno ->
        Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val e = estado) {
                MisModelosUiState.Cargando -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { contentDescription = "Cargando tus modelos" }
                )

                MisModelosUiState.Error -> Aviso(
                    titulo = "Error de conexión",
                    detalle = "No pudimos acceder a tu librería de modelos. Inténtalo nuevamente.",
                    accion = "Reintentar",
                    alPulsar = vm::cargar,
                )

                MisModelosUiState.Vacio -> AvisoVacio(
                    titulo = "Tu estudio está vacío",
                    detalle = "Convierte tu primera foto en un modelo 3D detallado.",
                    accion = "Crear modelo 3D",
                    alPulsar = alCrear,
                )

                is MisModelosUiState.ConDatos -> ListaModelosGrid(e.modelos, alAbrirModelo)
            }
        }
    }
}

@Composable
private fun ListaModelosGrid(modelos: List<Modelo3D>, alAbrirModelo: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(modelos, key = { it.id }) { modelo ->
            TarjetaModelo(modelo, alAbrirModelo)
        }
    }
}

@Composable
private fun TarjetaModelo(modelo: Modelo3D, onClick: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(modelo.id) }
    ) {
        Column {
            // Imagen Placeholder con Gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
            ) {
                // Carga asíncrona de la miniatura
                val previewBitmap by produceState<ImageBitmap?>(initialValue = null, modelo.id) {
                    value = AppContenedor.gestorPreviews.cargarBitmap(modelo.id)?.asImageBitmap()
                }

                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap!!,
                        contentDescription = "Vista previa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Etiqueta .GLB superior
                if (modelo.tieneArchivo3D) {
                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Text(
                            ".GLB",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                // Placeholder centrado
                Box(
                    modifier = Modifier.align(Alignment.Center).size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                )
            }

            // Datos
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = modelo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatearFecha(modelo.creadoEnMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (modelo.estado != EstadoModelo.LISTO) {
                        EtiquetaEstado(modelo.estado)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorEscenario(alElegir: (Escenario) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { abierto = true }) { 
            Icon(Icons.Default.BugReport, contentDescription = "Escenario", tint = MaterialTheme.colorScheme.onBackground) 
        }
        DropdownMenu(
            expanded = abierto, 
            onDismissRequest = { abierto = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Escenario.entries.forEach { escenario ->
                DropdownMenuItem(
                    text = { Text(escenario.nombreVisible(), color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        abierto = false
                        alElegir(escenario)
                    },
                )
            }
        }
    }
}

private fun Escenario.nombreVisible(): String = when (this) {
    Escenario.CON_DATOS -> "Datos"
    Escenario.VACIO -> "Vacía"
    Escenario.FALLA -> "Falla"
    Escenario.CARGANDO -> "Cargando"
}
