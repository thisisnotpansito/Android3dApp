package com.example.android3dapp.ui.visor

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window

fun capturarVista(view: View, window: Window, onCaptura: (Bitmap?) -> Unit) {
    if (view.width == 0 || view.height == 0) {
        onCaptura(null)
        return
    }
    
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            
            val x = locationOfViewInWindow[0]
            val y = locationOfViewInWindow[1]
            val rect = android.graphics.Rect(x, y, x + view.width, y + view.height)
            
            PixelCopy.request(window, rect, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    // Recortar al cuadrado (aspecto 1:1) para la tarjeta
                    val size = Math.min(bitmap.width, bitmap.height)
                    val xOffset = (bitmap.width - size) / 2
                    val yOffset = (bitmap.height - size) / 2
                    val squareBitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
                    onCaptura(squareBitmap)
                } else {
                    onCaptura(null)
                }
            }, Handler(Looper.getMainLooper()))
        } else {
            // Fallback (menos confiable con SurfaceView pero necesario para API < 26)
            view.isDrawingCacheEnabled = true
            val cache = view.drawingCache
            if (cache != null) {
                val size = Math.min(cache.width, cache.height)
                val xOffset = (cache.width - size) / 2
                val yOffset = (cache.height - size) / 2
                val squareBitmap = Bitmap.createBitmap(cache, xOffset, yOffset, size, size)
                onCaptura(squareBitmap)
            } else {
                onCaptura(null)
            }
            view.isDrawingCacheEnabled = false
        }
    } catch (e: Exception) {
        onCaptura(null)
    }
}
