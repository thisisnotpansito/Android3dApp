package com.example.android3dapp.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android3dapp.data.fotos.LectorFotos
import com.example.android3dapp.ui.detalle.DetalleModeloScreen
import com.example.android3dapp.ui.detalle.DetalleModeloViewModel
import com.example.android3dapp.ui.inicio.InicioScreen
import com.example.android3dapp.ui.modelos.MisModelosScreen
import com.example.android3dapp.ui.modelos.MisModelosViewModel
import com.example.android3dapp.ui.nuevo.NuevoModeloScreen
import com.example.android3dapp.ui.nuevo.NuevoModeloViewModel
import com.example.android3dapp.ui.visor.VisorModeloScreen
import com.example.android3dapp.ui.visor.VisorViewModel

/** Rutas de la app. El detalle se abre por id: `modelos/{id}`; el visor, `modelos/{id}/visor`. */
object Rutas {
    const val INICIO = "inicio"
    const val MODELOS = "modelos"
    const val DETALLE = "modelos/{id}"
    const val VISOR = "modelos/{id}/visor"
    const val NUEVO = "nuevo"
    fun detalle(id: String) = "modelos/$id"
    fun visor(id: String) = "modelos/$id/visor"
}

@Composable
fun Navegacion() {
    val nav = rememberNavController()
    val context = LocalContext.current
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
            DetalleModeloScreen(vm = vm, alVolver = { nav.popBackStack() }, alVer360 = { nav.navigate(Rutas.visor(it)) })
        }
        composable(Rutas.VISOR, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entrada ->
            val id = entrada.arguments?.getString("id").orEmpty()
            val vm: VisorViewModel = viewModel(
                factory = viewModelFactory { initializer { VisorViewModel(id, AppContenedor.repositorio, context.cacheDir) } },
            )
            VisorModeloScreen(vm = vm, titulo = "Vista 360°", alVolver = { nav.popBackStack() })
        }
        composable(Rutas.NUEVO) {
            val vm: NuevoModeloViewModel = viewModel(
                factory = viewModelFactory { initializer { NuevoModeloViewModel(AppContenedor.generador, AppContenedor.repositorio) } },
            )
            val lector = remember { LectorFotos(context.applicationContext) }
            NuevoModeloScreen(
                vm = vm,
                lector = lector,
                alVolver = { nav.popBackStack() },
                // Al crear, el formulario se cierra y se abre el detalle del modelo nuevo.
                alCrear = { id -> nav.navigate(Rutas.detalle(id)) { popUpTo(Rutas.NUEVO) { inclusive = true } } },
            )
        }
    }
}
