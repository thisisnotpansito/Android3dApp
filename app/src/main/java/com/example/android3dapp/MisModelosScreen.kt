package com.example.android3dapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisModelosScreen() {
    val repositorio = remember { RepositorioSimulado() }
    var estadoUi by remember { mutableStateOf(Escenario.CARGANDO) }
    var listaModelos by remember { mutableStateOf<List<Modelo3D>>(emptyList()) }
    var menuExpandido by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Función para recargar la información (Como el vm.cargar() del examen)
    fun cargarDatos() {
        estadoUi = Escenario.CARGANDO
        coroutineScope.launch {
            try {
                listaModelos = repositorio.obtenerModelos()
                estadoUi = if(listaModelos.isEmpty()) Escenario.VACIO else Escenario.LISTO
            } catch (e: Exception) {
                estadoUi = Escenario.ERROR
            }
        }
    }

    // Carga inicial
    LaunchedEffect(Unit) { cargarDatos() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Modelos 3D") },
                actions = {
                    // Selector de situaciones (Requisito del paso 7.2)
                    Box {
                        Button(onClick = { menuExpandido = true }) { Text("Escenario") }
                        DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                            Escenario.values().forEach { escenario ->
                                DropdownMenuItem(
                                    text = { Text(escenario.name) },
                                    onClick = {
                                        repositorio.escenarioActual = escenario
                                        menuExpandido = false
                                        cargarDatos()
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            // El switch obligatorio con las 4 situaciones
            when (estadoUi) {
                Escenario.CARGANDO -> CircularProgressIndicator()

                Escenario.ERROR -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No pudimos leer tus proyectos guardados.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { cargarDatos() }) { Text("Reintentar") }
                }

                Escenario.VACIO -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aún no tienes modelos 3D.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* Navegar al lienzo 3D */ }) { Text("Crear el primero") }
                }

                Escenario.LISTO -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(listaModelos) { modelo ->
                        ListItem(
                            headlineContent = { Text(modelo.nombre) },
                            supportingContent = { Text("${modelo.poligonos} polígonos • ${modelo.fecha}") },
                            modifier = Modifier.clickable {
                                // Acción requerida: tocar fila abre detalle
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}