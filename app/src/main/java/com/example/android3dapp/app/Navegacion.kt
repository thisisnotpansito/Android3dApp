package com.example.android3dapp.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android3dapp.ui.detalle.DetalleModeloScreen
import com.example.android3dapp.ui.detalle.DetalleModeloViewModel
import com.example.android3dapp.ui.inicio.InicioScreen
import com.example.android3dapp.ui.modelos.MisModelosScreen
import com.example.android3dapp.ui.modelos.MisModelosViewModel
import com.example.android3dapp.ui.nuevo.NuevoModeloScreen

/** Rutas de la app. El detalle se abre por id: `modelos/{id}`. */
object Rutas {
    const val INICIO = "inicio"
    const val MODELOS = "modelos"
    const val DETALLE = "modelos/{id}"
    const val NUEVO = "nuevo"
    fun detalle(id: String) = "modelos/$id"
}

@Composable
fun Navegacion() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            InicioScreen(
                alVerModelos = { nav.navigate(Rutas.MODELOS) },
                alCrear = { nav.navigate(Rutas.NUEVO) },
            )
        }
        composable(Rutas.MODELOS) {
            val vm: MisModelosViewModel = viewModel(
                factory = viewModelFactory { initializer { MisModelosViewModel(AppContenedor.repositorio) } },
            )
            MisModelosScreen(
                vm = vm,
                alAbrirModelo = { id -> nav.navigate(Rutas.detalle(id)) },
                alCrear = { nav.navigate(Rutas.NUEVO) },
                alVolver = { nav.popBackStack() },
            )
        }
        composable(Rutas.DETALLE, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entrada ->
            val id = entrada.arguments?.getString("id").orEmpty()
            val vm: DetalleModeloViewModel = viewModel(
                factory = viewModelFactory { initializer { DetalleModeloViewModel(id, AppContenedor.repositorio) } },
            )
            DetalleModeloScreen(vm = vm, alVolver = { nav.popBackStack() })
        }
        composable(Rutas.NUEVO) {
            NuevoModeloScreen(alVolver = { nav.popBackStack() })
        }
    }
}
