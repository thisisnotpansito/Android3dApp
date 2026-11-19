# Contraste WCAG 2.2 de los pares de color reales de Creative 3D Builder.
# Colores: tema propio (ui/theme/Color.kt), EtiquetaEstado (ui/comun/Componentes.kt) y los
# tokens base de Material 3 que la app no cambia (superficie, texto, error).
def lum(h):
    h = h.lstrip('#'); c = [int(h[i:i+2], 16) / 255 for i in (0, 2, 4)]
    c = [x / 12.92 if x <= 0.04045 else ((x + 0.055) / 1.055) ** 2.4 for x in c]
    return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2]
def ratio(a, b):
    la, lb = sorted((lum(a), lum(b)), reverse=True); return (la + 0.05) / (lb + 0.05)

pares = [
 ("Claro", "Botón principal: texto blanco sobre índigo", "#FFFFFF", "#4F46E5", "texto"),
 ("Claro", "Botón de texto «Volver»/«Escenario»: índigo sobre superficie", "#4F46E5", "#FEF7FF", "texto"),
 ("Claro", "Texto principal sobre superficie", "#1D1B20", "#FEF7FF", "texto"),
 ("Claro", "Texto secundario (fechas, ayudas) sobre superficie", "#49454F", "#FEF7FF", "texto"),
 ("Claro", "Error (mensajes de error) sobre superficie", "#B3261E", "#FEF7FF", "texto"),
 ("Claro", "Pastilla «Listo»", "#065F46", "#D1FAE5", "texto"),
 ("Claro", "Pastilla «Procesando»", "#92400E", "#FEF3C7", "texto"),
 ("Claro", "Pastilla «Con error»", "#991B1B", "#FEE2E2", "texto"),
 ("Claro", "Pastilla «Borrador»", "#374151", "#E5E7EB", "texto"),
 ("Claro", "Borde del campo de texto (contorno) sobre superficie", "#79747E", "#FEF7FF", "control"),
 ("Oscuro", "Botón principal: texto sobre índigo claro", "#381E72", "#C7D2FE", "texto"),
 ("Oscuro", "Botón de texto: índigo claro sobre superficie oscura", "#C7D2FE", "#141218", "texto"),
 ("Oscuro", "Texto principal sobre superficie oscura", "#E6E0E9", "#141218", "texto"),
 ("Oscuro", "Texto secundario sobre superficie oscura", "#CAC4D0", "#141218", "texto"),
 ("Oscuro", "Pastilla «Listo» (mismos colores en oscuro)", "#065F46", "#D1FAE5", "texto"),
]
print("| Tema | Par | Texto | Fondo | Contraste | Mínimo | ¿Pasa? |")
print("|---|---|---|---|---|---|---|")
for tema, nombre, t, f, tipo in pares:
    r = ratio(t, f); minimo = 4.5 if tipo == "texto" else 3.0
    print(f"| {tema} | {nombre} | `{t}` | `{f}` | {r:.1f}:1 | {minimo}:1 | {'Sí' if r >= minimo else '**No**'} |")
