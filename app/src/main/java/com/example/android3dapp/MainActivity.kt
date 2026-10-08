package com.example.android3dapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val engine = rememberEngine()
            // Esta lista guardará todos los objetos que el usuario cree
            val childNodes = remember { mutableStateListOf<Node>() }

            // Un "Box" nos permite apilar cosas una encima de otra
            Box(modifier = Modifier.fillMaxSize()) {


                // --- CAPA 1 (Fondo): El lienzo 3D ---
                Scene(
                    modifier = Modifier.fillMaxSize(),
                    engine = engine,
                    childNodes = childNodes,
                    // NUEVO: Detectar qué objeto se tocó (Raycasting)
                    onTouchEvent = { _, hitResult ->
                        // hitResult contiene el resultado del "rayo" que lanzamos.
                        // Si no chocó con nada, es nulo (null). Si chocó con algo, lo borramos.
                        if (hitResult != null) {
                            childNodes.remove(hitResult.node)
                        }

                        // Retornamos 'false' para que el motor sepa que no queremos bloquear
                        // el movimiento y aún queremos poder rotar la cámara por el escenario.
                        false
                    }
                )

                // --- CAPA 2 (Frente): Interfaz de Usuario ---
                Button(
                    onClick = {
                        // Acción al presionar el botón: Crear un cubo nuevo
                        val newCube = CubeNode(
                            engine = engine,
                            size = Size(x = 1.0f, y = 1.0f, z = 1.0f),
                            // Usamos Random para que aparezcan en posiciones ligeramente distintas
                            // y puedas notar que se están creando varios
                            center = Position(
                                x = Random.nextFloat() * 4f - 2f,
                                y = Random.nextFloat() * 4f - 2f,
                                z = -5.0f
                            )
                        )
                        // Añadimos el cubo a la escena
                        childNodes.add(newCube)
                    },
                    // Posicionamos el botón en la parte inferior central
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Text("Añadir Cubo")
                }
            }
        }
    }
}