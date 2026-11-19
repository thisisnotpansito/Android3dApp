package com.example.android3dapp.ui.visor

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android3dapp.app.AppContenedor
import com.example.android3dapp.ui.comun.Aviso
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberEnvironmentLoader
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun VisorModeloScreen(vm: VisorViewModel, titulo: String, alVolver: () -> Unit) {
    val estado by vm.estado.collectAsState()
    
    // Fondos de estudio
    val bgColor = when ((estado as? VisorUiState.Listo)?.entornoIndex) {
        1 -> Color(0xFF8A92A3) // Neutro
        2 -> Color(0xFFF1F5F9) // Claro
        else -> Color(0xFF13151A) // Oscuro
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
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
            is VisorUiState.Listo -> Visor3DInmersivo(vm, e, titulo, alVolver, snackbarHostState)
        }
        
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun Visor3DInmersivo(
    vm: VisorViewModel, 
    estado: VisorUiState.Listo, 
    titulo: String, 
    alVolver: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val cameraNode = rememberCameraNode(engine) { position = Position(z = 3.5f) }
    
    val nodos = rememberNodes {
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(estado.archivo),
                scaleToUnits = 1.0f,
                centerOrigin = Position(0f, -0.1f, 0f),
            ),
        )
    }

    val cameraManipulator = rememberCameraManipulator(
        orbitHomePosition = cameraNode.worldPosition,
        targetPosition = Position(0f, -0.1f, 0f),
    )
    
    // Entornos de iluminación IBL preestablecidos (Usando los nativos genéricos o ajustando color)
    LaunchedEffect(estado.entornoIndex) {
        try {
            val env = environmentLoader.createHDREnvironment(
                assetFileLocation = "sceneview/environments/syferfontein_1d_clear_1k.hdr"
            )
            // SceneView 2.x usually assigns environment differently, ignoring if throws
        } catch(e: Exception) {}
    }
    
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    
    // Obtenemos window iterando el context
    var context = LocalContext.current
    var window: android.view.Window? = null
    while (context is android.content.ContextWrapper) {
        if (context is Activity) {
            window = context.window
            break
        }
        context = context.baseContext
    }
    
    // Captura inicial automática de la portada (al entrar, si no tiene preview)
    var yaCapturado by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!yaCapturado) {
            kotlinx.coroutines.delay(1000) // Esperar a que el modelo cargue y se renderice
            if (window != null) {
                capturarVista(view, window) { bmp ->
                    if (bmp != null) {
                        scope.launch {
                            AppContenedor.gestorPreviews.guardarPreview(vm.getModeloId(), bmp)
                            yaCapturado = true
                        }
                    }
                }
            }
        }
    }
    
    // Auto rotación
    LaunchedEffect(estado.autoRotate) {
        if (estado.autoRotate) {
            while (true) {
                kotlinx.coroutines.delay(16)
                // Usando un manipulador custom para girar continuamente no es posible sin cambiar el target
                // Simulación de autoRotate: (Filament nativo requiere acceso a un update loop)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Render 3D de fondo
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            childNodes = nodos,
            cameraManipulator = cameraManipulator,
        )

        // Overlay Completo (Inmersive Mode lo esconde todo)
        AnimatedVisibility(
            visible = !estado.immersive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize()) {
                // UI Superpuesta (Overlays)
                TopBarPro(titulo, alVolver, vm::toggleImmersive, modifier = Modifier.align(Alignment.TopCenter))
                
                // Píldora de perspectiva superior izquierda
                Surface(
                    color = Color(0xFF1E2128),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.TopStart).padding(top = 100.dp, start = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("360° Studio", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
        
                // Interfaz Inferior
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Toolbar principal de herramientas
                    HerramientasToolbar(
                        onAutoRotar = vm::toggleAutoRotate,
                        isAutoRotando = estado.autoRotate,
                        onEntorno = vm::cycleEntorno,
                        entornoIndex = estado.entornoIndex,
                        onCentrar = {
                            // Simulamos centrado
                        },
                        onCapturar = {
                            if (window != null) {
                                capturarVista(view, window) { bmp ->
                                    if (bmp != null) {
                                        scope.launch {
                                            AppContenedor.gestorPreviews.guardarPreview(vm.getModeloId(), bmp)
                                            snackbarHostState.showSnackbar("Portada actualizada con éxito")
                                        }
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar("Error al capturar la portada") }
                                    }
                                }
                            }
                        }
                    )
                    
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
        
        // Botón transparente para volver a mostrar la UI si estaba en modo inmersivo
        if (estado.immersive) {
            Box(Modifier.fillMaxSize().clickable { vm.toggleImmersive() })
        }
    }
}

@Composable
private fun TopBarPro(titulo: String, alVolver: () -> Unit, onImmersive: () -> Unit, modifier: Modifier = Modifier) {
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
                Surface(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(" .GLB ", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
                Text("Visor Activo", color = Color.LightGray, fontSize = 12.sp)
            }
        }
        Surface(
            color = Color(0xFF252932),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(40.dp),
            onClick = onImmersive
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Fullscreen, contentDescription = "Inmersivo", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun HerramientasToolbar(
    onAutoRotar: () -> Unit,
    isAutoRotando: Boolean,
    onEntorno: () -> Unit,
    entornoIndex: Int,
    onCentrar: () -> Unit,
    onCapturar: () -> Unit
) {
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
            // Auto Rotate
            ItemHerramienta("Rotar", Icons.Default.OpenWith, isAutoRotando, onAutoRotar)
            // Entorno (Luces)
            val iconEntorno = when(entornoIndex) {
                1 -> Icons.Default.Lightbulb // Neutro
                2 -> Icons.Default.ColorLens // Claro
                else -> Icons.Default.Layers // Oscuro
            }
            ItemHerramienta("Entorno", iconEntorno, entornoIndex != 0, onEntorno)
            // Centrar
            ItemHerramienta("Centrar", Icons.Default.AddBox, false, onCentrar)
            // Capturar Portada
            ItemHerramienta("Portada", Icons.Default.CameraAlt, false, onCapturar)
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
