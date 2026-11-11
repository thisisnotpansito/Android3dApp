# INV-05 · Elegir el servicio de imagen a 3D

> Entregable 2 · issue #23. Decide cómo se convierte la foto en un modelo 3D (HU-02).

## Veredicto

**Se genera en el teléfono, con un método propio («inflado de silueta»).** El servicio de IA que se revisó exige un plan
pagado para usar su API, y el equipo no tiene presupuesto aprobado. La app queda preparada para cambiar de generador sin
tocar las pantallas: es otra implementación del contrato `GeneradorModelo3D`.

## Qué se revisó

| Opción | Calidad | Costo | ¿Sirve hoy? | Fuente |
|---|---|---|---|---|
| **Meshy** · API *Image to 3D* | Alta: reconstruye el objeto completo con textura | La API **exige plan pagado**: el plan gratis no permite crear claves ni tareas. Un modelo cuesta 20–30 créditos | **No**, sin presupuesto | Documentación oficial de Meshy (docs.meshy.ai), consultada el 11 de noviembre de 2026 |
| **Inflado de silueta** (propio, en el teléfono) | Media: separa el objeto del fondo y lo infla como un globo con su forma; la parte de atrás es un espejo de la de adelante | Gratis; sin internet | **Sí** | `data/generacion/MallaInflada.kt` |
| Otros servicios (Tripo, Stable Fast 3D…) | Por verificar | Por verificar | Por verificar | No se revisaron en este sprint |

## Cómo funciona el inflado de silueta

1. **Separa el objeto del fondo.** Toma el color medio del borde de la foto como color del fondo y lo «inunda» desde
   los bordes. Lo que no se alcanza es el objeto. Los huecos grandes del color del fondo rodeados por el objeto, como el
   asa de una taza, también se quitan.
2. **Mide la distancia al borde** de cada punto del objeto (distancia de chaflán 3-4).
3. **Infla:** el grosor crece con esa distancia en un perfil circular, adelante y atrás. En el borde el grosor es cero,
   así que la malla queda cerrada.
4. **Escribe un `.glb`** (glTF 2.0 binario) con la foto como textura, adelante y atrás.

Resultado con las dos fotos de demostración: **8 192 y 8 476 polígonos**, archivos de **256 y 272 KB**. En el emulador,
generar y guardar tomó menos de 7 segundos. En el Xiaomi está **sin medir** (RC-03).

**Límites, dichos con honestidad:** no reconstruye lo que la foto no muestra. La parte de atrás repite la de adelante, y
funciona mejor con el objeto centrado sobre un fondo liso. Si no encuentra un objeto, hace una lámina con la foto entera.

## Qué cambiaría con presupuesto

Se crea una clase `GeneradorRemoto : GeneradorModelo3D` que envía la foto a la API propia (TEC-05). La API llama al
servicio con su clave guardada como secreto del Worker, y la tabla `trabajos_generacion` ya tiene las columnas
`proveedor`, `id_externo`, `estado` y `mensaje_error` para seguir la tarea. Ni las pantallas ni el ViewModel cambian.
