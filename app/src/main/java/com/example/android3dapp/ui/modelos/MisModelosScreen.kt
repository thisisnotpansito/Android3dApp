package com.example.android3dapp.ui.modelos

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.android3dapp.data.Escenario
import com.example.android3dapp.domain.Modelo3D
import com.example.android3dapp.ui.comun.Aviso
import com.example.android3dapp.ui.comun.EtiquetaEstado
import com.example.android3dapp.ui.comun.formatearFecha
import com.example.android3dapp.ui.comun.formatearPoligonos

/** HU-05 · «Mis Modelos 3D» con sus cuatro estados. Muestra, no decide: lee el ViewModel. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisModelosScreen(
    vm: MisModelosViewModel,
    alAbrirModelo: (String) -> Unit,
    alCrear: () -> Unit,
    alVolver: () -> Unit,
) {
    val estado by vm.estado.collectAsState()
    val esDepuracion = (LocalContext.current.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Modelos 3D") },
                navigationIcon = { TextButton(onClick = alVolver) { Text("Inicio") } },
                actions = { if (esDepuracion && vm.admiteEscenarios) SelectorEscenario(vm::elegirEscenario) },
            )
        },
    ) { relleno ->
        Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val e = estado) {
                MisModelosUiState.Cargando ->
                    CircularProgressIndicator(Modifier.semantics { contentDescription = "Cargando tus modelos" })

                MisModelosUiState.Error -> Aviso(
                    titulo = "No pudimos leer tus modelos",
                    detalle = "Revisa tu conexión o el permiso de almacenamiento e inténtalo otra vez.",
                    accion = "Reintentar",
                    alPulsar = vm::cargar,
                )

                MisModelosUiState.Vacio -> Aviso(
                    titulo = "Aún no tienes modelos 3D",
                    detalle = "Toma una foto de un objeto y la convertiremos en un modelo que puedes girar.",
                    accion = "Crear el primero",
                    alPulsar = alCrear,
                )

                is MisModelosUiState.ConDatos -> ListaModelos(e.modelos, alAbrirModelo)
            }
        }
    }
}

@Composable
private fun ListaModelos(modelos: List<Modelo3D>, alAbrirModelo: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        items(modelos, key = { it.id }) { modelo ->
            ListItem(
                headlineContent = { Text(modelo.nombre) },
                supportingContent = {
                    Text("${formatearFecha(modelo.creadoEnMillis)} · ${formatearPoligonos(modelo.poligonos)}")
                },
                trailingContent = { EtiquetaEstado(modelo.estado) },
                modifier = Modifier.clickable { alAbrirModelo(modelo.id) },
            )
            HorizontalDivider()
        }
    }
}

/** Selector de las cuatro situaciones. Solo aparece en compilaciones de depuración. */
@Composable
private fun SelectorEscenario(alElegir: (Escenario) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { abierto = true }) { Text("Escenario") }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            Escenario.entries.forEach { escenario ->
                DropdownMenuItem(
                    text = { Text(escenario.nombreVisible()) },
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
    Escenario.CON_DATOS -> "Con datos"
    Escenario.VACIO -> "Vacía"
    Escenario.FALLA -> "Falla"
    Escenario.CARGANDO -> "Cargando"
}
