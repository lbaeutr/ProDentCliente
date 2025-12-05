# Directorio DI (Dependency Injection)

Este directorio contiene los módulos de inyección de dependencias usando Hilt (Dagger Hilt).

## Archivos

### DataModule.kt
**Función**: Módulo Hilt para inyección de dependencias de datos.

**Scope**: `@InstallIn(SingletonComponent::class)` - Instancias singleton

**Provee**:
- `DataStore<Preferences>`: Almacenamiento de preferencias
- `UserPreferences`: Gestión de preferencias del usuario
- `AuthRepository`: Repositorio de autenticación

**Ejemplo**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: AuthApiService,
        preferences: UserPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, preferences)
    }
}
```

---

### NetworkModule.kt
**Función**: Módulo Hilt para inyección de dependencias de red.

**Scope**: `@InstallIn(SingletonComponent::class)` - Instancias singleton

**Provee**:
- `OkHttpClient`: Cliente HTTP con interceptores
- `Retrofit`: Cliente Retrofit configurado
- `AuthApiService`: Service de API de autenticación
- Interceptores (Auth, Logging)

**Ejemplo**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(preferences: UserPreferences): AuthInterceptor {
        return AuthInterceptor(preferences)
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) 
                HttpLoggingInterceptor.Level.BODY 
            else 
                HttpLoggingInterceptor.Level.NONE
        }
    }
}
```

---

## ¿Qué es Dependency Injection?

**Dependency Injection (DI)** es un patrón de diseño donde los objetos reciben sus dependencias desde fuera en lugar de crearlas ellas mismas.

### Sin DI (Mal):
```kotlin
class AuthViewModel {
    private val repository = AuthRepositoryImpl(
        AuthApiService(),
        UserPreferences()
    )
}
```

### Con DI (Bien):
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
```

---

## Hilt (Dagger Hilt)

### ¿Por qué Hilt?

1. **Compilación**: Genera código en tiempo de compilación
2. **Type-Safe**: Errores detectados en compilación
3. **Android-Specific**: Optimizado para Android
4. **Scopes**: Manejo automático de ciclos de vida
5. **Testing**: Fácil reemplazar dependencias en tests

### Componentes de Hilt:

#### 1. Application Class
```kotlin
@HiltAndroidApp
class ProdentApplication : Application()
```

#### 2. Modules
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MyModule {
    @Provides
    fun provideSomething(): Something = SomethingImpl()
}
```

#### 3. Injection
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase
) : ViewModel()
```

---

## Scopes en Hilt

### SingletonComponent
- **Ciclo de vida**: Toda la aplicación
- **Uso**: Repositorios, APIs, DataStore
- **Ejemplo**: AuthRepository, Retrofit

### ViewModelComponent
- **Ciclo de vida**: Mientras vive el ViewModel
- **Uso**: UseCases específicos de ViewModel
- **Ejemplo**: LoginUseCase, LogoutUseCase

### ActivityComponent
- **Ciclo de vida**: Mientras vive la Activity
- **Uso**: Dependencias específicas de Activity
- **Ejemplo**: Raro en Compose

---

## Interceptores

### AuthInterceptor
**Función**: Añade el token JWT a todas las peticiones.

```kotlin
class AuthInterceptor(
    private val preferences: UserPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferences.getToken()
        val request = chain.request().newBuilder()
        
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(request.build())
    }
}
```

### LoggingInterceptor
**Función**: Log de requests y responses (solo en modo debug).

```kotlin
HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) 
        HttpLoggingInterceptor.Level.BODY 
    else 
        HttpLoggingInterceptor.Level.NONE
}
```

---

## Ventajas de Usar DI

### 1. Testeable
```kotlin
// En test, inyectar mock
@Test
fun test() {
    val mockRepo = mock<AuthRepository>()
    val viewModel = AuthViewModel(mockRepo)
    // ...
}
```

### 2. Desacoplamiento
```kotlin
// ViewModel no conoce implementación
class AuthViewModel(
    private val repository: AuthRepository  // Interface
)
```

### 3. Reutilización
```kotlin
// Misma instancia en toda la app
@Singleton
fun provideRetrofit(): Retrofit
```

### 4. Mantenibilidad
```kotlin
// Cambiar implementación en un solo lugar
@Provides
fun provideAuthRepository(): AuthRepository {
    return NewAuthRepositoryImpl()  // Cambio aquí
}
```

### 5. Configuración Centralizada
```kotlin
// Toda la configuración de red en un módulo
object NetworkModule {
    // Timeouts, interceptores, base URL, etc.
}
```

---

## Flujo de Inyección

```
1. Application (@HiltAndroidApp)
    ↓
2. Modules (@Module + @InstallIn)
    ↓
3. Providers (@Provides)
    ↓
4. Injection Site (@Inject constructor)
    ↓
5. Use (ViewModel, Repository, etc.)
```

---

## Mejores Prácticas

1. **Un módulo por capa**: DataModule, NetworkModule, UseCaseModule
2. **Usar Interfaces**: Proveer interfaces, no implementaciones
3. **Scopes apropiados**: Singleton para repos, ViewModelScoped para UseCases
4. **Qualifiers para múltiples**: @Named, @Qualifier custom
5. **Lazy injection**: Usar Provider<T> si no siempre se necesita

---

## Testing con Hilt

### HiltAndroidTest
```kotlin
@HiltAndroidTest
class AuthViewModelTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: AuthRepository
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun test() {
        // repository está inyectado automáticamente
    }
}
```

### Reemplazar dependencias en tests
```kotlin
@UninstallModules(DataModule::class)
@HiltAndroidTest
class MyTest {
    
    @Module
    @InstallIn(SingletonComponent::class)
    object TestDataModule {
        @Provides
        fun provideMockRepository(): AuthRepository = mock()
    }
}
```

---

## Documentación Oficial

- [Hilt Android](https://developer.android.com/training/dependency-injection/hilt-android)
- [Hilt Testing](https://developer.android.com/training/dependency-injection/hilt-testing)

---

**Última actualización**: 2025-10-28
