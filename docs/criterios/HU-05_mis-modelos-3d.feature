# language: es
Característica: HU-05 · Lista de Mis Modelos 3D
  Como estudiante de ingeniería, quiero ver la lista de mis modelos 3D
  para retomar los que ya hice.

  Escenario: Lista con datos (éxito)
    Dado que tengo modelos 3D creados
    Cuando abro «Mis Modelos 3D»
    Entonces los veo del más reciente al más antiguo
    Y cada uno muestra su nombre, su fecha, su tamaño en polígonos y su estado

  Escenario: No se pueden leer (error)
    Dado que la app no puede leer mis modelos
    Cuando abro «Mis Modelos 3D»
    Entonces veo el mensaje «No pudimos leer tus modelos»
    Y un botón «Reintentar» que vuelve a cargar la lista

  Escenario: Primera instalación (celular · lista vacía)
    Dado que acabo de instalar la app y no tengo modelos
    Cuando abro «Mis Modelos 3D»
    Entonces veo el mensaje «Aún no tienes modelos 3D»
    Y un botón «Crear el primero» que me lleva al formulario de nuevo modelo

  Escenario: Mientras carga (celular · conexión lenta en el bus)
    Dado que la lista tarda en llegar
    Cuando abro «Mis Modelos 3D»
    Entonces veo un indicador de carga y nunca una pantalla en blanco
