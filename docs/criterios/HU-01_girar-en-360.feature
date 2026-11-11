# language: es
Característica: HU-01 · Girar el modelo en 360°
  Como estudiante, quiero girar el modelo con el dedo
  para verlo desde todos sus ángulos.

  Escenario: Girar y acercar (éxito)
    Dado que abrí el detalle de un modelo «Listo»
    Cuando toco «Girar en 360°» y arrastro el dedo
    Entonces el modelo gira y puedo ver su parte de atrás
    Y si pellizco la pantalla, se acerca o se aleja

  Escenario: El archivo no carga (error)
    Dado que el archivo 3D no se puede descargar
    Cuando abro el visor
    Entonces veo «No pudimos abrir el modelo 3D» y «Reintentar»

  Escenario: Modelo sin archivo 3D (celular)
    Dado un modelo de ejemplo que todavía no tiene archivo 3D
    Cuando abro su detalle
    Entonces el botón «Girar en 360°» aparece desactivado y dice «sin archivo 3D»
