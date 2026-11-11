package com.example.android3dapp.ui.nuevo

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.android3dapp.data.fotos.LectorFotos
import java.io.File

/**
 * HU-07 · Elegir la foto con la cámara o la galería. HU-02 · Generar el modelo 3D.
 * HU-08 · Ver en qué va. HU-14 · Reintentar si falla.
 *
 * La cámara se abre con la app de cámara del teléfono (no hace falta el permiso CAMERA)
 * y la galería con el selector de fotos de Android (tampoco pide permiso de almacenamiento).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoModeloScreen(vm: NuevoModeloViewModel, lector: LectorFotos, alVolver: () -> Unit, alCrear: (String) -> Unit) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current
    var uriCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.elegirFoto { lector.leer(uri) }
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = uriCamara
        if (ok && uri != null) vm.elegirFoto { lector.leer(uri) }
    }
    LaunchedEffect(ui.fase) { (ui.fase as? Fase.Listo)?.let { alCrear(it.id) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo modelo") },
                navigationIcon = { TextButton(onClick = alVolver, enabled = !ui.ocupado) { Text("Volver") } },
            )
        },
    ) { relleno ->
        Column(
            Modifier.padding(relleno).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val vista = remember(ui.foto) { ui.foto?.let { BitmapFactory.decodeByteArray(it.jpeg, 0, it.jpeg.size)?.asImageBitmap() } }
            OutlinedCard(
                Modifier.fillMaxWidth().aspectRatio(1.3f),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        ui.fase == Fase.LeyendoFoto -> CircularProgressIndicator()
                        vista != null -> Image(vista, contentDescription = "Foto elegida", contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                        else -> Text(
                            "Toma una foto del objeto o elígela de la galería.\nMejor si está sobre un fondo liso.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val archivo = File(context.cacheDir, "fotos").apply { mkdirs() }.let { File(it, "captura-${System.currentTimeMillis()}.jpg") }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.archivos", archivo)
                        uriCamara = uri
                        camara.launch(uri)
                    },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f),
                ) { Text("Tomar foto") }
                OutlinedButton(
                    onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f),
                ) { Text("Galería") }
            }
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = vm::cambiarNombre,
                label = { Text("Nombre del modelo") },
                supportingText = { Text("Te quedan ${NuevoModeloViewModel.MAX_NOMBRE - ui.nombre.length} caracteres") },
                singleLine = true,
                enabled = !ui.ocupado,
                modifier = Modifier.fillMaxWidth(),
            )
            ui.falta?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }

            EstadoFase(ui.fase)

            Button(onClick = vm::generar, enabled = !ui.ocupado, modifier = Modifier.fillMaxWidth()) {
                Text(if (ui.fase is Fase.Error && ui.foto != null) "Reintentar" else "Generar modelo 3D")
            }
        }
    }
}

@Composable
private fun EstadoFase(fase: Fase) {
    val (texto, color) = when (fase) {
        Fase.Generando -> "Generando el modelo 3D en tu teléfono…" to MaterialTheme.colorScheme.secondaryContainer
        Fase.Guardando -> "Guardando tu modelo…" to MaterialTheme.colorScheme.secondaryContainer
        is Fase.Error -> fase.mensaje to MaterialTheme.colorScheme.errorContainer
        else -> return
    }
    Card(colors = CardDefaults.cardColors(containerColor = color)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (fase !is Fase.Error) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.5.dp)
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
