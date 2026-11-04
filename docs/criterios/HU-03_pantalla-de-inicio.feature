# language: es
Característica: HU-03 · Pantalla de inicio
  Como estudiante de ingeniería, quiero ver al abrir la app qué puedo hacer con ella
  para empezar sin que nadie me explique.

  Escenario: Primer vistazo (éxito)
    Dado que abro la app
    Cuando aparece la pantalla de inicio
    Entonces veo el nombre de la app y los tres pasos: tomar una foto, girarla en 360° y verla en mi espacio

  Escenario: Función que aún no existe (error de expectativa)
    Dado que estoy en la pantalla de inicio
    Cuando toco «Crear desde una foto»
    Entonces veo el formulario de nuevo modelo con un aviso de que generar el 3D llega en el entregable 2
    Y el botón «Generar modelo 3D» aparece desactivado

  Escenario: Pantalla pequeña (celular)
    Dado un teléfono de 360 dp de ancho
    Cuando abro la pantalla de inicio
    Entonces todo se lee sin cortarse y puedo desplazarme hasta los dos botones
