# Directorio UI/Theme

Este directorio contiene la configuración del tema Material Design 3 de la aplicación.

## Archivos

### Color.kt
**Función**: Define la paleta de colores de ProDent.

**Colores Principales**:
```kotlin
// Colores de Marca
val ProdentGreen = Color(0xFF26D2A0)        // Verde turquesa principal
val ProdentGreenDark = Color(0xFF1DB88A)    // Verde oscuro
val ProdentGreenLight = Color(0xFF5FDDBA)   // Verde claro
val ProdentGreenPale = Color(0xFFE5F9F3)    // Verde muy claro (fondos)

// Colores Secundarios
val ProdentBlue = Color(0xFF4A90E2)         // Azul
val ProdentOrange = Color(0xFFFF9500)       // Naranja (alertas)

// Colores de Estado
val Success = ProdentGreen                   // Éxito
val Error = Color(0xFFDC2626)               // Error
val Warning = Color(0xFFF59E0B)             // Advertencia
val Info = Color(0xFF3B82F6)                // Información
```

**Tema Claro y Oscuro**:
- Colores adaptativos para cada modo
- Alto contraste en textos
- Accesibilidad WCAG AA

---

### ColorExtensions.kt
**Función**: Extensiones y utilidades para colores.

**Funciones**:
- Conversión de colores
- Cálculo de contraste
- Generación de variantes
- Helpers para gradientes

---

### Type.kt
**Función**: Define la tipografía de la aplicación usando la fuente Lato.

**Familia de Fuente**:
```kotlin
val FamiliaFuenteLato = FontFamily(
    Font(R.font.lato_thin, FontWeight.Thin),
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_bold, FontWeight.Bold),
    Font(R.font.lato_black, FontWeight.Black),
    Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.lato_bolditalic, FontWeight.Bold, FontStyle.Italic)
)
```

**Escalas Tipográficas**:

| Estilo | Tamaño | Peso | Uso |
|--------|--------|------|-----|
| displayLarge | 57sp | Black | Títulos muy grandes |
| displayMedium | 45sp | Black | Títulos grandes |
| displaySmall | 36sp | Black | Títulos medianos |
| headlineLarge | 32sp | Bold | Headers principales |
| headlineMedium | 28sp | Bold | Headers secundarios |
| headlineSmall | 24sp | Bold | Títulos de sección |
| titleLarge | 22sp | Normal | Títulos de card |
| titleMedium | 16sp | Normal | Botones, tabs |
| titleSmall | 14sp | Normal | Subtítulos |
| bodyLarge | 16sp | Normal | Texto principal grande |
| bodyMedium | 14sp | Normal | Texto principal |
| bodySmall | 12sp | Normal | Texto secundario |
| labelLarge | 14sp | Normal | Labels grandes |
| labelMedium | 12sp | Normal | Labels medianos |
| labelSmall | 11sp | Normal | Captions, hints |

---

### Theme.kt
**Función**: Configura el tema completo de Material Design 3.

**Temas Disponibles**:

#### Tema Claro (Light):
```kotlin
private val LightColorScheme = lightColorScheme(
    primary = ProdentGreen,
    onPrimary = Color.White,
    primaryContainer = ProdentGreenPale,
    onPrimaryContainer = ProdentGreenDark,
    
    secondary = ProdentBlue,
    onSecondary = Color.White,
    
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF6B6B6B)
)
```

#### Tema Oscuro (Dark):
```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = ProdentGreenLight,
    onPrimary = Color(0xFF003826),
    primaryContainer = Color(0xFF005138),
    onPrimaryContainer = ProdentGreenPale,
    
    secondary = Color(0xFF7DB3F5),
    onSecondary = Color(0xFF003256),
    
    error = Color(0xFFEF4444),
    onError = Color(0xFF5F0000),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE5E5E5),
    
    surface = Color(0xFF242424),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFB0B0B0)
)
```

**Composable del Tema**:
```kotlin
@Composable
fun ProdentClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Tipografia,
        content = content
    )
}
```

---

## Material Design 3 (Material You)

### Características Implementadas:

1. **Color Schemes**: Paleta completa de colores
2. **Typography**: Sistema tipográfico escalable
3. **Shapes**: Bordes redondeados consistentes
4. **Elevation**: Sistema de elevación con sombras
5. **Dynamic Color**: Soporte para colores dinámicos (Android 12+)

### Tokens de Color:

- **Primary**: Color principal de la marca
- **Secondary**: Color secundario de apoyo
- **Tertiary**: Color terciario (opcional)
- **Error**: Color para errores
- **Background**: Color de fondo
- **Surface**: Color de superficies (cards, sheets)
- **Outline**: Bordes y divisores

---

## Uso en Componentes

### Acceder a Colores:

```kotlin
@Composable
fun MyComponent() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    
    Box(
        modifier = Modifier.background(backgroundColor)
    ) {
        Text(
            text = "Hola",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
```

### Acceder a Tipografía:

```kotlin
@Composable
fun MyText() {
    Text(
        text = "Título",
        style = MaterialTheme.typography.headlineMedium
    )
    
    Text(
        text = "Cuerpo",
        style = MaterialTheme.typography.bodyMedium
    )
}
```

---

## Modo Oscuro

### Detección Automática:

```kotlin
ProdentClientTheme(
    darkTheme = isSystemInDarkTheme()  // Detecta automáticamente
) {
    // Contenido
}
```

### Forzar Modo:

```kotlin
// Siempre claro
ProdentClientTheme(darkTheme = false) { }

// Siempre oscuro
ProdentClientTheme(darkTheme = true) { }
```

---

## Ventajas del Tema Centralizado

### 1. Consistencia
- Mismo aspecto en toda la app
- Colores y tipografía uniformes

### 2. Mantenibilidad
- Cambiar color en un lugar
- Actualiza toda la app

### 3. Accesibilidad
- Contraste adecuado
- Tamaños de fuente escalables
- Modo oscuro para reducir fatiga

### 4. Branding
- Colores de marca consistentes
- Identidad visual fuerte

### 5. Adaptabilidad
- Modo oscuro/claro
- Dynamic color en Android 12+
- Responsive a preferencias del sistema

---

## Mejores Prácticas

### 1. Siempre Usar MaterialTheme

❌ **Incorrecto**:
```kotlin
Text(
    text = "Hola",
    color = Color(0xFF26D2A0)  // Color hardcodeado
)
```

✅ **Correcto**:
```kotlin
Text(
    text = "Hola",
    color = MaterialTheme.colorScheme.primary
)
```

### 2. Usar Tokens Semánticos

❌ **Incorrecto**:
```kotlin
Box(modifier = Modifier.background(Color.Red))  // Color directo
```

✅ **Correcto**:
```kotlin
Box(modifier = Modifier.background(MaterialTheme.colorScheme.error))
```

### 3. Pares de Colores

Siempre usar color + onColor:
```kotlin
Surface(
    color = MaterialTheme.colorScheme.primary
) {
    Text(
        "Texto visible",
        color = MaterialTheme.colorScheme.onPrimary  // Contraste garantizado
    )
}
```

---

## Testing del Tema

### Preview con Temas:

```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyComponentPreview() {
    ProdentClientTheme {
        MyComponent()
    }
}
```

---

## Recursos Externos

- [Material Design 3](https://m3.material.io/)
- [Color System](https://m3.material.io/styles/color/system)
- [Typography](https://m3.material.io/styles/typography/overview)

---

**Última actualización**: 2025-10-28

