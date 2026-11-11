package com.example.android3dapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.android3dapp.app.AppContenedor
import com.example.android3dapp.app.Navegacion
import com.example.android3dapp.ui.theme.Android3dAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContenedor.iniciar(applicationContext)
        enableEdgeToEdge()
        setContent {
            Android3dAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Navegacion()
                }
            }
        }
    }
}
