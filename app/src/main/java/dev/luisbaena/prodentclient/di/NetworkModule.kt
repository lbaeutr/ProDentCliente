package dev.luisbaena.prodentclient.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.luisbaena.prodentclient.data.remote.api.AuthApiService
import dev.luisbaena.prodentclient.data.remote.api.ClinicApiService
import dev.luisbaena.prodentclient.data.remote.api.DentistApiService
import dev.luisbaena.prodentclient.data.remote.api.MaterialApiService
import dev.luisbaena.prodentclient.data.remote.api.WorkApiService
import dev.luisbaena.prodentclient.data.remote.api.WorkTypeApiService
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import dev.luisbaena.prodentclient.domain.usecase.user.ChangePasswordUseCase
import dev.luisbaena.prodentclient.domain.usecase.user.GetProfileUseCase
import dev.luisbaena.prodentclient.domain.usecase.user.RegisterUseCase
import dev.luisbaena.prodentclient.domain.usecase.user.UpdateProfileUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.CreateClinicaUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.GetClinicaByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.GetClinicasUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.UpdateClinicaUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.CreateDentistUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.GetDentistByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.GetDentistsUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.UpdateDentistUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.CreateMaterialUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.GetMaterialByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.GetMaterialsUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.UpdateMaterialUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.AssignProsthetistUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.ChangeWorkStatusUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.CreateWorkUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.DeleteWorkImageUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.GetWorkByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.GetWorkByNumberUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.GetWorksUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.MarkWorkAsPaidUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.UpdateWorkImageMetadataUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.UpdateWorkUseCase
import dev.luisbaena.prodentclient.domain.usecase.work.UploadWorkImageUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.CreateWorkTypeUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.GetWorkTypeByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.GetWorkTypesUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.UpdateWorkTypeUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Funcion principal: Proveer las dependencias relacionadas con la red.
 *  provideJson: Provee una instancia de Json configurada para la serialización/deserialización de JSON.
 *  provideHttpLoggingInterceptor: Provee un interceptor de logging para las solicitudes HTTP.
 *  provideOkHttpClient: Provee una instancia de OkHttpClient configurada con el interceptor de logging y tiempos de espera, que permite realizar solicitudes HTTP.
 *  provideRetrofit : Provee una instancia de Retrofit configurada con la URL base, el cliente OkHttp y el convertidor de JSON, que facilita la comunicación con APIs REST.
 *  provideAuthApiService: Provee una instancia de AuthApiService creada a partir de Retrofit, que define los endpoints de autenticación.
 *  provideRegisterUseCase : Provee una instancia de RegisterUseCase que maneja la lógica de registro de usuarios.
 *  provideGetProfileUseCase : Provee una instancia de GetProfileUseCase que maneja la lógica para obtener el perfil del usuario.
 *  provideUpdateProfileUseCase : Provee una instancia de UpdateProfileUseCase que maneja la lógica para actualizar el perfil del usuario.
 *  provideChangePasswordUseCase : Provee una instancia de ChangePasswordUseCase que maneja la lógica para cambiar la contraseña del usuario.
 * Provee casos de uso relacionados con clínicas, dentistas y tipos de trabajo, facilitando la interacción con los repositorios correspondientes.
 * Cada función está anotada con @Provides y @Singleton para indicar que son proveedores de dependencias únicas dentro del contenedor de Hilt.
 * Cada función de caso de uso recibe el repositorio correspondiente como parámetro, lo que permite la inyección de dependencias y facilita la prueba y el mantenimiento del código.
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    // CONFIGURACIÓN DE RED
    private const val BASE_URL = "https://prodent-api.onrender.com"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // Permite ignorar campos desconocidos en el JSON
        coerceInputValues =
            true // Intenta convertir valores que no coinciden exactamente con el tipo esperado
        isLenient = true // Permite un análisis más flexible del JSON
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                HttpLoggingInterceptor.Level.BODY // Registra el cuerpo completo de las solicitudes y respuestas HTTP
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Agrega el interceptor de logging
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
            .readTimeout(60, TimeUnit.SECONDS) // Tiempo de espera para leer datos (aumentado para imágenes)
            .writeTimeout(60, TimeUnit.SECONDS) // Tiempo de espera para escribir datos (aumentado para uploads, imagenes)
            .callTimeout(120, TimeUnit.SECONDS) // Timeout total de la llamada (aumentado para uploads grandes, imagenes)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Establece la URL base para las solicitudes HTTP
            .client(okHttpClient) // Usa el cliente OkHttp configurado
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())) // Usa el convertidor de JSON
            .build()
    }

    // =============
    // API SERVICES
    // =============

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java) // Crea una implementación de AuthApiService usando Retrofit
    }

    @Provides
    @Singleton
    fun provideClinicApiService(retrofit: Retrofit): ClinicApiService {
        return retrofit.create(ClinicApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDentistApiService(retrofit: Retrofit): DentistApiService {
        return retrofit.create(DentistApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideWorkTypeApiService(retrofit: Retrofit): WorkTypeApiService {
        return retrofit.create(WorkTypeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMaterialApiService(retrofit: Retrofit): MaterialApiService {
        return retrofit.create(MaterialApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkApiService(retrofit: Retrofit): WorkApiService {
        return retrofit.create(WorkApiService::class.java)
    }

    // ==========
    // USE CASES
    // ==========

    @Provides
    fun provideGetProfileUseCase(authRepository: AuthRepository): GetProfileUseCase {
        return GetProfileUseCase(authRepository)
    }

    @Provides
    fun provideUpdateProfileUseCase(authRepository: AuthRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(authRepository)
    }

    @Provides
    fun provideChangePasswordUseCase(authRepository: AuthRepository): ChangePasswordUseCase {
        return ChangePasswordUseCase(authRepository)
    }

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideCreateClinicaUseCase(
        clinicRepository: ClinicRepository
    ): CreateClinicaUseCase {
        return CreateClinicaUseCase(clinicRepository)
    }

    @Provides
    @Singleton
    fun provideGetClinicasUseCase(
        clinicRepository: ClinicRepository
    ): GetClinicasUseCase {
        return GetClinicasUseCase(clinicRepository)
    }

    @Provides
    @Singleton
    fun provideGetClinicaByIdUseCase(
        clinicRepository: ClinicRepository
    ): GetClinicaByIdUseCase {
        return GetClinicaByIdUseCase(clinicRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateClinicaUseCase(
        clinicRepository: ClinicRepository
    ): UpdateClinicaUseCase {
        return UpdateClinicaUseCase(clinicRepository)
    }

    // UseCases de Dentistas
    @Provides
    @Singleton
    fun provideCreateDentistUseCase(
        dentistRepository: DentistRepository
    ): CreateDentistUseCase {
        return CreateDentistUseCase(dentistRepository)
    }

    @Provides
    @Singleton
    fun provideGetDentistsUseCase(
        dentistRepository: DentistRepository
    ): GetDentistsUseCase {
        return GetDentistsUseCase(dentistRepository)
    }

    @Provides
    @Singleton
    fun provideGetDentistByIdUseCase(
        dentistRepository: DentistRepository
    ): GetDentistByIdUseCase {
        return GetDentistByIdUseCase(dentistRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDentistUseCase(
        dentistRepository: DentistRepository
    ): UpdateDentistUseCase {
        return UpdateDentistUseCase(dentistRepository)
    }

    @Provides
    @Singleton
    fun provideGetWorkTypesUseCase(
        workTypeRepository: WorkTypeRepository
    ): GetWorkTypesUseCase {
        return GetWorkTypesUseCase(workTypeRepository)
    }

    @Provides
    @Singleton
    fun provideGetWorkTypeByIdUseCase(
        workTypeRepository: WorkTypeRepository
    ): GetWorkTypeByIdUseCase {
        return GetWorkTypeByIdUseCase(workTypeRepository)
    }

    @Provides
    @Singleton
    fun provideCreateWorkTypeUseCase(
        workTypeRepository: WorkTypeRepository
    ): CreateWorkTypeUseCase {
        return CreateWorkTypeUseCase(workTypeRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateWorkTypeUseCase(
        workTypeRepository: WorkTypeRepository
    ): UpdateWorkTypeUseCase {
        return UpdateWorkTypeUseCase(workTypeRepository)
    }

    @Provides
    @Singleton
    fun provideGetMaterialsUseCase(
        materialRepository: MaterialRepository
    ): GetMaterialsUseCase {
        return GetMaterialsUseCase(materialRepository)
    }

    @Provides
    @Singleton
    fun provideGetMaterialByIdUseCase(
        materialRepository: MaterialRepository
    ): GetMaterialByIdUseCase {
        return GetMaterialByIdUseCase(materialRepository)
    }

    @Provides
    @Singleton
    fun provideCreateMaterialUseCase(
        materialRepository: MaterialRepository
    ): CreateMaterialUseCase {
        return CreateMaterialUseCase(materialRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateMaterialUseCase(
        materialRepository: MaterialRepository
    ): UpdateMaterialUseCase {
        return UpdateMaterialUseCase(materialRepository)
    }

    //CRUD BÁSICO de Trabajos!!!!!!!!!!!!!!
    @Provides
    @Singleton
    fun provideCreateWorkUseCase(
        workRepository: WorkRepository
    ): CreateWorkUseCase {
        return CreateWorkUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideGetWorksUseCase(
        workRepository: WorkRepository
    ): GetWorksUseCase {
        return GetWorksUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideGetWorkByIdUseCase(
        workRepository: WorkRepository
    ): GetWorkByIdUseCase {
        return GetWorkByIdUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideGetWorkByNumberUseCase(
        workRepository: WorkRepository
    ): GetWorkByNumberUseCase {
        return GetWorkByNumberUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateWorkUseCase(
        workRepository: WorkRepository
    ): UpdateWorkUseCase {
        return UpdateWorkUseCase(workRepository)
    }

    //  OPERACIONES ESPECÍFICAS DE TRABAJOS
    @Provides
    @Singleton
    fun provideChangeWorkStatusUseCase(
        workRepository: WorkRepository
    ): ChangeWorkStatusUseCase {
        return ChangeWorkStatusUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideAssignProsthetistUseCase(
        workRepository: WorkRepository
    ): AssignProsthetistUseCase {
        return AssignProsthetistUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideMarkWorkAsPaidUseCase(
        workRepository: WorkRepository
    ): MarkWorkAsPaidUseCase {
        return MarkWorkAsPaidUseCase(workRepository)
    }

    // PROVEEDORES DE USECASES - GESTIÓN DE IMÁGENES
    @Provides
    @Singleton
    fun provideUploadWorkImageUseCase(
        workRepository: WorkRepository
    ): UploadWorkImageUseCase {
        return UploadWorkImageUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteWorkImageUseCase(
        workRepository: WorkRepository
    ): DeleteWorkImageUseCase {
        return DeleteWorkImageUseCase(workRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateWorkImageMetadataUseCase(
        workRepository: WorkRepository
    ): UpdateWorkImageMetadataUseCase {
        return UpdateWorkImageMetadataUseCase(workRepository)
    }
}