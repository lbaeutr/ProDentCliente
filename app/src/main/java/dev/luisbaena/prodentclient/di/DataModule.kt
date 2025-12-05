package dev.luisbaena.prodentclient.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.luisbaena.prodentclient.data.local.preferencias.DirectoryPreferences
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.AuthApiService
import dev.luisbaena.prodentclient.data.remote.api.ClinicApiService
import dev.luisbaena.prodentclient.data.remote.api.DentistApiService
import dev.luisbaena.prodentclient.data.remote.api.MaterialApiService
import dev.luisbaena.prodentclient.data.remote.api.WorkApiService
import dev.luisbaena.prodentclient.data.remote.api.WorkTypeApiService
import dev.luisbaena.prodentclient.data.repository.AuthRepositoryImpl
import dev.luisbaena.prodentclient.data.repository.ClinicRepositoryImpl
import dev.luisbaena.prodentclient.data.repository.DentistRepositoryImpl
import dev.luisbaena.prodentclient.data.repository.MaterialRepositoryImpl
import dev.luisbaena.prodentclient.data.repository.WorkRepositoryImpl
import dev.luisbaena.prodentclient.data.repository.WorkTypeRepositoryImpl
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import javax.inject.Singleton
/**
    Módulo de inyección de dependencias para los repositorios y preferencias de datos.
    Proporciona instancias singleton de UserPreferences, AuthRepository, ClinicRepository,
    DentistRepository, WorkTypeRepository, MaterialRepository ,WorkRepository y  DirectoryPreferences.
 */


@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // Provide UserPreferences
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    // Provide AuthRepository
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: AuthApiService,
        userPreferences: UserPreferences,
        directoryPreferences: DirectoryPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, userPreferences, directoryPreferences)
    }
    // Provide ClinicRepository
    @Provides
    @Singleton
    fun provideClinicRepository(
        apiService: ClinicApiService,
        userPreferences: UserPreferences
    ): ClinicRepository {
        return ClinicRepositoryImpl(apiService, userPreferences)
    }

    // Provide DentistRepository
    @Provides
    @Singleton
    fun provideDentistRepository(
        apiService: DentistApiService,
        userPreferences: UserPreferences
    ): DentistRepository {
        return DentistRepositoryImpl(apiService, userPreferences)
    }

    // Provide WorkTypeRepository
    @Provides
    @Singleton
    fun provideWorkTypeRepository(
        apiService: WorkTypeApiService,
        userPreferences: UserPreferences
    ): WorkTypeRepository {
        return WorkTypeRepositoryImpl(apiService, userPreferences)
    }
    // Provide MaterialRepository
    @Provides
    @Singleton
    fun provideMaterialRepository(
        materialApiService: MaterialApiService,
        userPreferences: UserPreferences
    ): MaterialRepository {
        return MaterialRepositoryImpl(materialApiService, userPreferences)
    }

    // Provide WorkRepository
    @Provides
    @Singleton
     fun providerWorkRepository(
        apiService: WorkApiService,
        userPreferences: UserPreferences
    ): WorkRepository {
        return WorkRepositoryImpl(apiService, userPreferences)
    }
}