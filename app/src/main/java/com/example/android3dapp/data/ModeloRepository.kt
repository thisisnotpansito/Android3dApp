package com.example.android3dapp.data

import com.example.android3dapp.domain.Modelo3D

/**
 * TEC-02 · Contrato de datos. Los ViewModel solo conocen esta interfaz, nunca
 * una implementación concreta.
 *
 * Hoy la cumple [RepositorioSimulado]. En el entregable 2 la cumplirá un
 * repositorio remoto que habla con la API, y **ninguna pantalla cambiará**:
 * solo cambia la línea que elige la implementación en `app/AppContenedor.kt`.
 */
interface ModeloRepository {

    /** Todos los modelos del usuario. Lanza [ErrorDeLectura] si no se pueden leer. */
    suspend fun obtenerTodos(): List<Modelo3D>

    /** Un modelo por su id, o `null` si no existe. Lanza [ErrorDeLectura] si falla. */
    suspend fun obtenerPorId(id: String): Modelo3D?
}

/** Los datos no se pudieron leer. El ViewModel lo convierte en el estado «error». */
class ErrorDeLectura(mensaje: String) : Exception(mensaje)
