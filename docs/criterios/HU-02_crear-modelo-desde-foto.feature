# language: es
Característica: HU-02 · Crear un modelo 3D a partir de una foto
  Como estudiante, quiero convertir la foto en un modelo 3D
  para no tener que modelarlo a mano.

  Escenario: Modelo generado (éxito)
    Dado que elegí una foto de un objeto sobre un fondo liso y le puse nombre
    Cuando toco «Generar modelo 3D»
    Entonces veo «Generando el modelo 3D…» y luego el detalle del modelo nuevo
    Y el modelo aparece de primero en «Mis Modelos 3D», con estado «Listo» y sus polígonos

  Escenario: Falta algo (error)
    Dado que no elegí foto o no escribí el nombre
    Cuando toco «Generar modelo 3D»
    Entonces veo qué falta («Falta la foto», «Falta el nombre») y no se pierde lo que ya llené

  Escenario: Se guardó mal (celular · sin conexión) · HU-14
    Dado que el modelo se generó pero no se pudo guardar
    Cuando toco «Reintentar»
    Entonces se guarda sin volver a generarlo

  # RC-03 (eficiencia): el modelo se genera en menos de 10 s en un celular de gama media
  Escenario: Tiempo de generación
    Dado una foto de 1600 px en un Android de gama media
    Cuando genero el modelo
    Entonces está listo en menos de 10 segundos
