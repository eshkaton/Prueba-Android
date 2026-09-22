# Registro Multimedia

App Android (Java) para registrar a los integrantes de un equipo, valorar su
progreso y guardar notas de voz.

## Flujo de pantallas

```
LoginActivity  ──(credenciales correctas)──>  WelcomeActivity
     │                                              │
     └──> RegisterActivity                          │
                                                    │
   barra de menú inferior ───────────────────────────┤
     · Inicio      → InicioFragment (bienvenida)     │
     · Equipo      → IntegranteFragment              │
     · Agregar     → FormularioFragment              │
     · Progreso    → ProgresoFragment                │
                                                    │
   barra de menú superior ──────────────────────────┘
     · Grabar audio      → GrabacionAudioActivity
     · Mis grabaciones   → ReproductorAudioActivity
     · Cerrar sesión     → vuelve al login y limpia la pila
```

Desde el listado del equipo se abre `DetalleIntegranteActivity` con la ficha
del integrante.

## Cuenta de prueba

`admin` / `1234`. También puedes crear una cuenta desde el registro; las
cuentas viven en memoria mientras la app está abierta.

## Sistema de diseño

| Dónde | Qué contiene |
|---|---|
| `res/values/colors.xml` | paleta «Aurora» (violeta, turquesa, coral, neutros) |
| `res/values/themes.xml` | roles de color de Material 3, tema claro |
| `res/values-night/themes.xml` | los mismos roles en oscuro |
| `res/values/type.xml` | familias tipográficas |
| `res/values/styles.xml` | tipografía, botones, tarjetas, campos, barras |
| `res/values/dimens.xml` | escala de espaciado y tamaños |

Los layouts no llevan colores escritos a mano: usan `?attr/colorPrimary`,
`?attr/colorSurface`, etc. Por eso el modo oscuro funciona sin duplicar
ningún layout.

### Cambiar la tipografía

`res/values/type.xml` es el único sitio donde se nombran las fuentes. Ahora
usa familias del sistema (Roboto y sus variantes), que funcionan sin
conexión y en cualquier dispositivo. Para usar una fuente propia:

1. copia el `.ttf` en `res/font/` (por ejemplo `res/font/poppins.ttf`);
2. cambia el valor en `type.xml` por `@font/poppins`.

Toda la app la adopta sin tocar ningún layout.

## Compilar

Requiere Android Studio con JDK 17 o superior. El proyecto usa Gradle 8.11.1
y el plugin de Android 8.8.0.

```bash
./gradlew assembleDebug
```

---

## Cómo abrir este proyecto

1. Android Studio → **File › Open…** y elige la carpeta `RegistroMultimedia`
   (la que contiene `settings.gradle.kts`), no una subcarpeta.
2. Acepta el *Gradle Sync* que aparece arriba. La primera vez descarga
   Gradle 8.11.1 y las dependencias de AndroidX, así que tarda unos minutos.
3. Ejecuta con el botón ▶ sobre un emulador o dispositivo con **Android 12
   (API 31) o superior**, que es el `minSdk` del proyecto.

Si Gradle se queja del JDK, comprueba en
*Settings › Build, Execution, Deployment › Build Tools › Gradle* que el
*Gradle JDK* sea **17 o superior**.
