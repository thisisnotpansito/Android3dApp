package com.example.android3dapp.ui.inicio

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** HU-03 · Pantalla de inicio: qué hace la app y cómo empezar. */
@Composable
fun InicioScreen(alVerModelos: () -> Unit, alCrear: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Creative 3D Builder", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "De una foto a un modelo 3D que puedes girar y poner en tu espacio.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Paso(1, "Toma una foto", "Fotografía el objeto con la cámara o elígelo de la galería.", "Disponible")
        Paso(2, "Gíralo en 360°", "La app genera el modelo 3D y lo puedes ver desde todos sus ángulos.", "Disponible")
        Paso(3, "Míralo en tu espacio", "Ponlo sobre una mesa o el piso a través de la cámara.", "Entregable 3")
        Spacer(Modifier.height(8.dp))
        Button(onClick = alVerModelos, modifier = Modifier.fillMaxWidth()) { Text("Ver mis modelos 3D") }
        OutlinedButton(onClick = alCrear, modifier = Modifier.fillMaxWidth()) { Text("Crear desde una foto") }
    }
}

@Composable
private fun Paso(numero: Int, titulo: String, detalle: String, entrega: String) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$numero", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(entrega, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
