package com.example.android3dapp.ui.detalle

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android3dapp.app.AppContenedor
import com.example.android3dapp.data.ExportadorGlb
import com.example.android3dapp.domain.EstadoModelo
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.comun.Aviso
import com.example.android3dapp.ui.comun.EtiquetaEstado
import com.example.android3dapp.ui.comun.formatearFecha
import com.example.android3dapp.ui.comun.formatearPoligonos
import com.example.android3dapp.ui.theme.SuperficieElevada2
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleModeloScreen(vm: DetalleModeloViewModel, alVolver: () -> Unit, alVer360: (String) -> Unit) {
    val estado by vm.estado.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado) { if (estado == DetalleUiState.Borrado) alVolver() }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { 
                    IconButton(onClick = alVolver) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
    ) { relleno ->
        Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val e = estado) {
                DetalleUiState.Cargando, DetalleUiState.Borrado -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                DetalleUiState.Error -> Aviso("No pudimos abrir este modelo", "Algo falló al leerlo. Inténtalo otra vez.", "Reintentar", vm::cargar)
                DetalleUiState.NoEncontrado -> Aviso("Este modelo ya no existe", "Puede que se haya borrado.", "Volver", alVolver)
                is DetalleUiState.Listo -> {
                    ContenidoDetalle(
                        e, 
                        alVer360 = { alVer360(e.modelo.id) }, 
                        alBorrar = vm::borrar,
                        onExportar = { uri ->
                            scope.launch {
                                val out = context.contentResolver.openOutputStream(uri)
                                if (out != null) {
                                    val ok = ExportadorGlb.exportarAStream(context, e.modelo, AppContenedor.repositorio, out)
                                    snackbarHostState.showSnackbar(if (ok) "Exportado con éxito" else "Error al exportar")
                                }
                            }
                        },
                        onCompartir = {
                            scope.launch {
                                val intent = ExportadorGlb.crearIntentCompartir(context, e.modelo, AppContenedor.repositorio)
                                if (intent != null) context.startActivity(Intent.createChooser(intent, "Compartir .glb"))
                                else snackbarHostState.showSnackbar("Error al preparar archivo")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(
    e: DetalleUiState.Listo, 
    alVer360: () -> Unit, 
    alBorrar: () -> Unit,
    onExportar: (android.net.Uri) -> Unit,
    onCompartir: () -> Unit
) {
    val modelo = e.modelo
    var confirmar by remember { mutableStateOf(false) }
    
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("model/gltf-binary")) { uri ->
        if (uri != null) onExportar(uri)
    }
    
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Tarjeta Visual Principal
        Surface(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.2f),
            shape = RoundedCornerShape(24.dp),
            color = SuperficieElevada2,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(Modifier.fillMaxSize()) {
                val previewBitmap by produceState<ImageBitmap?>(initialValue = null, modelo.id) {
                    value = AppContenedor.gestorPreviews.cargarBitmap(modelo.id)?.asImageBitmap()
                }
                
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap!!,
                        contentDescription = "Previsualización",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Etiqueta superior
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Text("Formato GLTF 2.0 Binary", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                // Botón Play Central (CTA)
                if (modelo.tieneArchivo3D) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.Center).size(72.dp),
                        onClick = alVer360
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Visor 3D", modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                } else {
                    Text("Generando...", modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Título y Estado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(modelo.nombre, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EtiquetaEstado(modelo.estado)
                    Spacer(Modifier.width(12.dp))
                    Text(formatearFecha(modelo.creadoEnMillis), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        // Metadatos Compactos
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaInfo("Malla", formatearPoligonos(modelo.poligonos), Modifier.weight(1f))
            TarjetaInfo("Peso", if (modelo.tieneArchivo3D) "~2.5 MB" else "-", Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))

        // Actions
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { exportLauncher.launch("${modelo.nombre.replace(" ", "_")}.glb") },
                enabled = modelo.tieneArchivo3D,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuperficieElevada2)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Exportar")
            }
            Button(
                onClick = onCompartir,
                enabled = modelo.tieneArchivo3D,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuperficieElevada2)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Compartir")
            }
        }
        
        OutlinedButton(
            onClick = { confirmar = true },
            enabled = !e.borrando,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) { 
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Eliminar Proyecto") 
        }
    }

    if (confirmar) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { confirmar = false },
            title = { Text("¿Eliminar modelo?") },
            text = { Text("Se borrará el archivo 3D de tu dispositivo.") },
            confirmButton = { 
                Button(onClick = { confirmar = false; alBorrar() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Eliminar") } 
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurface) } },
        )
    }
}

@Composable
private fun TarjetaInfo(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(valor, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
