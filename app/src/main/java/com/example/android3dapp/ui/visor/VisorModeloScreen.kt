package com.example.android3dapp.ui.visor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

/** HU-01 · Visor 360°: arrastrar para girar, pellizcar para acercar. Usa SceneView (Filament). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisorModeloScreen(vm: VisorViewModel, titulo: String, alVolver: () -> Unit) {
    val estado by vm.estado.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = { TextButton(onClick = alVolver) { Text("Volver") } },
            )
        },
    ) { relleno ->
        Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val e = estado) {
                VisorUiState.Cargando -> CircularProgressIndicator()
                VisorUiState.Error -> Aviso(
                    titulo = "No pudimos abrir el modelo 3D",
                    detalle = "El archivo no se pudo descargar. Revisa tu conexión e inténtalo otra vez.",
                    accion = "Reintentar",
                    alPulsar = vm::cargar,
                )
                is VisorUiState.Listo -> Visor3D(e.archivo)
            }
        }
    }
}

@Composable
private fun Visor3D(archivo: File) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    // Lejos lo suficiente para que quepa entero en una pantalla vertical.
    val cameraNode = rememberCameraNode(engine) { position = Position(z = 3.2f) }
    val nodos = rememberNodes {
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(archivo),
                scaleToUnits = 1.0f,
                centerOrigin = Position(0f, 0f, 0f),
            ),
        )
    }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            childNodes = nodos,
            cameraManipulator = rememberCameraManipulator(
                orbitHomePosition = cameraNode.worldPosition,
                targetPosition = Position(0f, 0f, 0f),
            ),
        )
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            Text(
                "Arrastra para girar · pellizca para acercar",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}
