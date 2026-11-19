package com.example.android3dapp.ui.visor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android3dapp.ui.comun.Aviso
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import java.io.File

/** Visor 360° altamente inmersivo inspirado en herramientas profesionales (Spline/Nomad). */
@Composable
fun VisorModeloScreen(vm: VisorViewModel, titulo: String, alVolver: () -> Unit) {
    val estado by vm.estado.collectAsState()
    
    // El fondo base de la pantalla
    val bgColor = Color(0xFF13151A) // Gris muy oscuro, elegante
    
    Box(Modifier.fillMaxSize().background(bgColor)) {
        when (val e = estado) {
            VisorUiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF81E6D9)) }
            VisorUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Aviso(
                    titulo = "Error al abrir el modelo",
                    detalle = "No pudimos cargar el archivo 3D. Inténtalo de nuevo.",
                    accion = "Reintentar",
                    alPulsar = vm::cargar,
                )
            }
            is VisorUiState.Listo -> Visor3DInmersivo(e.archivo, titulo, alVolver)
        }
    }
}

@Composable
private fun Visor3DInmersivo(archivo: File, titulo: String, alVolver: () -> Unit) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val cameraNode = rememberCameraNode(engine) { position = Position(z = 4.0f) }
    
    val nodos = rememberNodes {
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(archivo),
                scaleToUnits = 1.2f, // Escala adaptada para que resalte
                centerOrigin = Position(0f, -0.2f, 0f),
            ),
        )
    }

    Box(Modifier.fillMaxSize()) {
        // Render 3D de fondo
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            childNodes = nodos,
            cameraManipulator = rememberCameraManipulator(
                orbitHomePosition = cameraNode.worldPosition,
                targetPosition = Position(0f, -0.2f, 0f),
            ),
        )

        // UI Superpuesta (Overlays)
        TopBarPro(titulo, alVolver, modifier = Modifier.align(Alignment.TopCenter))
        
        // Botones laterales (Deshacer, Rehacer, Capas)
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 100.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonRedondoFlotante(Icons.Default.Undo)
                BotonRedondoFlotante(Icons.Default.Redo)
            }
            BotonRedondoFlotante(Icons.Default.Layers)
            BotonRedondoFlotante(Icons.Default.Fullscreen)
        }
        
        // Píldora de perspectiva superior izquierda
        Surface(
            color = Color(0xFF1E2128),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.align(Alignment.TopStart).padding(top = 100.dp, start = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Perspective", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Interfaz Inferior
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Etiqueta de estado
            Surface(
                color = Color(0xFF252932),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF81E6D9), CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text("Escultura", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Text("|", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("1 seleccionado", color = Color.LightGray, fontSize = 12.sp)
                }
            }

            // Toolbar principal de herramientas
            HerramientasToolbar()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Texto de ayuda súper sutil
            Text(
                "Arrastra para girar · Pellizca para zoom",
                color = Color.Gray,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun TopBarPro(titulo: String, alVolver: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = alVolver) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF81E6D9), CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("Todos los cambios guardados", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Surface(
            color = Color(0xFF252932),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Color(0xFF81E6D9), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun BotonRedondoFlotante(icono: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = Color(0xFF252932).copy(alpha = 0.8f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(44.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun HerramientasToolbar() {
    var activo by remember { mutableStateOf("Mover") }
    
    Surface(
        color = Color(0xFF1E2128),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemHerramienta("Añadir", Icons.Default.AddBox, activo == "Añadir") { activo = "Añadir" }
            ItemHerramienta("Mover", Icons.Default.OpenWith, activo == "Mover") { activo = "Mover" }
            ItemHerramienta("Girar", Icons.Default.RotateRight, activo == "Girar") { activo = "Girar" }
            ItemHerramienta("Color", Icons.Default.ColorLens, activo == "Color") { activo = "Color" }
        }
    }
}

@Composable
private fun ItemHerramienta(
    texto: String, 
    icono: androidx.compose.ui.graphics.vector.ImageVector, 
    esActivo: Boolean, 
    alPulsar: () -> Unit
) {
    val bgColor = if (esActivo) Color(0xFF81E6D9) else Color.Transparent
    val tintColor = if (esActivo) Color(0xFF13151A) else Color.White
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = alPulsar)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = texto, tint = tintColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(texto, color = if (esActivo) Color.White else Color.Gray, fontSize = 11.sp)
    }
}
