package com.example.android3dapp.ui.comun

import com.example.android3dapp.domain.EstadoModelo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val ES = Locale.forLanguageTag("es-CO")

/** «8 oct 2026». Se usa SimpleDateFormat porque minSdk 24 no trae java.time. */
fun formatearFecha(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", ES).apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

fun formatearPoligonos(poligonos: Int?): String =
    if (poligonos == null) "Sin malla aún" else "%,d polígonos".format(ES, poligonos)

fun EstadoModelo.etiqueta(): String = when (this) {
    EstadoModelo.BORRADOR -> "Borrador"
    EstadoModelo.PROCESANDO -> "Procesando"
    EstadoModelo.LISTO -> "Listo"
    EstadoModelo.ERROR -> "Con error"
}
