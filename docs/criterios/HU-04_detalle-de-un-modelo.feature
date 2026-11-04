# language: es
Característica: HU-04 · Detalle de un modelo
  Como estudiante de ingeniería, quiero abrir uno de mis modelos
  para ver su estado, su fecha y su tamaño.

  Escenario: Abrir el detalle (éxito)
    Dado que estoy en «Mis Modelos 3D»
    Cuando toco un modelo
    Entonces veo su nombre, su estado, su fecha de creación, sus polígonos y su identificador

  Escenario: El modelo ya no existe (error)
    Dado que abro un modelo que fue borrado
    Cuando la app intenta leerlo
    Entonces veo el mensaje «Este modelo ya no existe»
    Y un botón para volver a «Mis Modelos 3D»

  Escenario: Volver a la lista (celular)
    Dado que abrí el detalle de un modelo
    Cuando toco «Volver» o el botón atrás del teléfono
    Entonces regreso a «Mis Modelos 3D» en la misma posición
