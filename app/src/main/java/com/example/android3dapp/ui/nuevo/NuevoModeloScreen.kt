package com.example.android3dapp.ui.nuevo

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.android3dapp.data.fotos.LectorFotos
import java.io.File

/**
 * HU-07 · Elegir la foto con la cámara o la galería. HU-02 · Generar el modelo 3D.
 * HU-08 · Ver en qué va. HU-14 · Reintentar si falla.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NuevoModeloScreen(vm: NuevoModeloViewModel, lector: LectorFotos, alVolver: () -> Unit, alCrear: (String) -> Unit) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
                title = { Text("Nuevo modelo", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { TextButton(onClick = alVolver, enabled = !ui.ocupado) { Text("Cancelar") } },
            )
        },
    ) { relleno ->
        Column(
            Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val vista = remember(ui.foto) { ui.foto?.let { BitmapFactory.decodeByteArray(it.jpeg, 0, it.jpeg.size)?.asImageBitmap() } }
            
            // Área de previsualización de la foto con mejor feedback visual
            OutlinedCard(
                Modifier.fillMaxWidth().aspectRatio(1.2f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, if (vista == null) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        ui.fase == Fase.LeyendoFoto -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Procesando imagen...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        vista != null -> {
                            Image(
                                bitmap = vista,
                                contentDescription = "Foto elegida",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                            )
                        }
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Toma una foto del objeto o elígela de la galería.\nMejor si está sobre un fondo liso y bien iluminado.",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Botones de acción principales
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val archivo = File(context.cacheDir, "fotos").apply { mkdirs() }.let { File(it, "captura-${System.currentTimeMillis()}.jpg") }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.archivos", archivo)
                        uriCamara = uri
                        camara.launch(uri)
                    },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { 
                    Text("Cámara") 
                }
                OutlinedButton(
                    onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { 
                    Text("Galería") 
                }
            }

            // Campo de texto con mejor UX (teclado inteligente)
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = vm::cambiarNombre,
                label = { Text("Nombre del modelo") },
                placeholder = { Text("Ej: Taza roja") },
                supportingText = { Text("${ui.nombre.length} / ${NuevoModeloViewModel.MAX_NOMBRE} caracteres") },
                singleLine = true,
                enabled = !ui.ocupado,
                isError = ui.falta != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Mensajes de error animados para no ser tan bruscos
            AnimatedVisibility(visible = ui.falta != null, enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ui.falta ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            EstadoFase(ui.fase)
            Spacer(Modifier.weight(1f))

            // Botón principal de generar, siempre visible y destacable
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    vm.generar() 
                }, 
                enabled = !ui.ocupado && ui.foto != null && ui.nombre.isNotBlank(), // Validación en tiempo real
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (ui.fase is Fase.Error && ui.foto != null) "Reintentar" else "Generar modelo 3D", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun EstadoFase(fase: Fase) {
    AnimatedVisibility(visible = fase is Fase.Generando || fase is Fase.Guardando || fase is Fase.Error, enter = fadeIn(), exit = fadeOut()) {
        val (texto, color, contentColor) = when (fase) {
            Fase.Generando -> Triple("Construyendo geometría 3D...", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
            Fase.Guardando -> Triple("Finalizando modelo...", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
            is Fase.Error -> Triple(fase.mensaje, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
            else -> return@AnimatedVisibility
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = color, contentColor = contentColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (fase !is Fase.Error) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp, color = contentColor)
                Text(texto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}