package com.example.android3dapp.ui.nuevo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val MAX_NOMBRE = 80

/**
 * Interfaz del formulario «Nuevo modelo». En la fase inicial solo se muestra:
 * elegir la foto y generar el 3D llegan en el entregable 2 (HU-07 y HU-02).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoModeloScreen(alVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo modelo") },
                navigationIcon = { TextButton(onClick = alVolver) { Text("Volver") } },
            )
        },
    ) { relleno ->
        Column(
            Modifier.padding(relleno).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedCard(
                Modifier.fillMaxWidth().aspectRatio(1.4f),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Aquí irá la foto del objeto\n(cámara o galería)",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            OutlinedTextField(
                value = nombre,
                onValueChange = { if (it.length <= MAX_NOMBRE) nombre = it },
                label = { Text("Nombre del modelo") },
                supportingText = { Text("Te quedan ${MAX_NOMBRE - nombre.length} caracteres") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text(
                    "Fase inicial: esta pantalla muestra cómo será el flujo. Tomar la foto y generar el modelo 3D llegan en el entregable 2.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Generar modelo 3D") }
        }
    }
}
