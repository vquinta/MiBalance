# MiBalance

App personal para Android que ajusta el balance izquierda/derecha (L-R) del audio según el perfil de cada auricular.

## ¿Para qué sirve?

Algunos auriculares suenan más fuerte por un lado que por el otro. MiBalance permite guardar perfiles de balance para cada modelo de auriculares y aplicarlos con un solo toque, en tiempo real.

## Funciones

- **Pestaña Perfiles**: crea perfiles eligiendo el modelo de auricular y configurando su balance L-R. Pulsa un perfil para activarlo; pulsa el activo de nuevo para desactivarlo.
- **Pestaña Balance**: slider que aplica el balance en tiempo real + botón "Reiniciar" para centrar.
- Los perfiles se guardan localmente en el dispositivo (sin cuenta ni conexión).

## Requisitos

- Android 9 (API 28) o superior (el balance se aplica mediante `DynamicsProcessing`).
- Compilación: Android Studio (AGP 9.5.2, Kotlin 2.2.10).

## Compilar

```bash
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Licencia

Uso personal.