# ProDent Client - Aplicación Android

Aplicación cliente Android para el sistema de gestión dental ProDent.

## Instalación y Configuración

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17 o superior
- Android SDK 34
- Gradle 8.0 o superior

### Compilar el Proyecto

```bash
# Clonar el repositorio
git clone [URL_DEL_REPO]
cd ProDent

# Compilar
.\gradlew assembleDebug

# Instalar en dispositivo
.\gradlew installDebug
```

### Ejecutar desde Android Studio

1. Abrir el proyecto en Android Studio
2. Sincronizar Gradle
3. Ejecutar la aplicación (Shift + F10)

## Configuración de API

Configurar la URL base de la API en `data/remote/ApiConfig.kt`:

```kotlin
const val BASE_URL = "https://tu-api.com/"
```

### Estándares de Código

- Seguir Clean Architecture
- Usar Compose para UI
- Mantener separación de responsabilidades

