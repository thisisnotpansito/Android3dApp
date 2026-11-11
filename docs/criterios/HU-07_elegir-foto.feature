# language: es
Característica: HU-07 · Elegir la foto con la cámara o la galería
  Como estudiante, quiero tomar la foto del objeto con la cámara o elegirla de la galería
  para crear su modelo 3D.

  Escenario: Elegir de la galería (éxito)
    Dado que estoy en «Nuevo modelo»
    Cuando toco «Galería» y elijo una foto
    Entonces veo la foto en el recuadro del formulario

  Escenario: Tomar la foto (éxito)
    Dado que estoy en «Nuevo modelo»
    Cuando toco «Tomar foto» y la cámara del teléfono la toma
    Entonces veo la foto en el recuadro del formulario

  Escenario: La foto no se puede leer (error)
    Dado que elijo un archivo que no es una imagen o está dañado
    Cuando la app intenta leerlo
    Entonces veo «No pudimos leer esa foto. Prueba con otra.»

  Escenario: Sin permisos que pedir (celular)
    Dado que es la primera vez que uso la app
    Cuando toco «Galería» o «Tomar foto»
    Entonces no aparece ninguna solicitud de permiso de almacenamiento ni de cámara
