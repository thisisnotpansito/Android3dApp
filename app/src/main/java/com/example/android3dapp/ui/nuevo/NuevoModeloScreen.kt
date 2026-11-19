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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.android3dapp.ui.theme.BordeSutil
import com.example.android3dapp.ui.theme.SuperficieElevada2
import java.io.File

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
                title = { Text("Nuevo Proyecto", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = alVolver, enabled = !ui.ocupado) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
    ) { relleno ->
        Column(
            Modifier
                .padding(relleno)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            val vista = remember(ui.foto) { ui.foto?.let { BitmapFactory.decodeByteArray(it.jpeg, 0, it.jpeg.size)?.asImageBitmap() } }
            
            // Área de previsualización
            OutlinedCard(
                Modifier.fillMaxWidth().aspectRatio(1.2f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, if (vista == null) BordeSutil else MaterialTheme.colorScheme.primary),
                colors = CardDefaults.outlinedCardColors(containerColor = SuperficieElevada2)
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
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                            )
                        }
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Selecciona una foto para iniciar la reconstrucción.",
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
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val archivo = File(context.cacheDir, "fotos").apply { mkdirs() }.let { File(it, "captura-${System.currentTimeMillis()}.jpg") }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.archivos", archivo)
                        uriCamara = uri
                        camara.launch(uri)
                    },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuperficieElevada2)
                ) { 
                    Icon(Icons.Default.AddAPhoto, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Cámara") 
                }
                Button(
                    onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !ui.ocupado,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuperficieElevada2)
                ) { 
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Galería") 
                }
            }

            // Campo de texto
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = vm::cambiarNombre,
                label = { Text("Nombre del proyecto") },
                placeholder = { Text("Ej: Taza roja") },
                singleLine = true,
                enabled = !ui.ocupado,
                isError = ui.falta != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            AnimatedVisibility(visible = ui.falta != null, enter = slideInVertically() + fadeIn(), exit = slideOutVertically() + fadeOut()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(ui.falta ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            EstadoFase(ui.fase)
            
            Spacer(Modifier.weight(1f))

            Button(
                onClick = { 
                    focusManager.clearFocus()
                    vm.generar() 
                }, 
                enabled = !ui.ocupado && ui.foto != null && ui.nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (ui.fase is Fase.Error && ui.foto != null) "Reintentar" else "Generar Modelo 3D", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun EstadoFase(fase: Fase) {
    AnimatedVisibility(visible = fase is Fase.Generando || fase is Fase.Guardando || fase is Fase.Error, enter = fadeIn(), exit = fadeOut()) {
        val (texto, color, contentColor) = when (fase) {
            Fase.Generando -> Triple("Procesando fotogrametría on-device...", MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), MaterialTheme.colorScheme.primary)
            Fase.Guardando -> Triple("Exportando archivo .glb...", SuperficieElevada2, MaterialTheme.colorScheme.onSurface)
            is Fase.Error -> Triple(fase.mensaje, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
            else -> return@AnimatedVisibility
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = color, contentColor = contentColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
        ) {
            Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (fase !is Fase.Error) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 3.dp, color = contentColor)
                else Icon(Icons.Default.Warning, contentDescription = null, tint = contentColor)
                Text(texto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}
