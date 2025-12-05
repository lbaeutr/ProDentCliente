package dev.luisbaena.prodentclient.data.local.preferencias

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.luisbaena.prodentclient.data.remote.dto.user.DirectoryUserDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

// DataStore para el cache del directorio
private val Context.directoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "directory_cache")

/**
 * Gestiona el cache del directorio de usuarios usando DataStore --> Almacenamiento local ligero y asíncrono
 */
@Singleton
class DirectoryPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Este objeto contiene las claves para los datos almacenados
    companion object {
        private val KEY_USERS_JSON = stringPreferencesKey("users_json")
        private val KEY_LAST_UPDATE = longPreferencesKey("last_update")
    }

    // Instancia de Gson para la serialización/deserialización JSON
    private val gson = Gson()

    /**
     * Guardar usuarios en cache
     */
    suspend fun saveUsers(users: List<DirectoryUserDto>) {
        context.directoryDataStore.edit { prefs ->
            val json = gson.toJson(users)
            prefs[KEY_USERS_JSON] = json
            prefs[KEY_LAST_UPDATE] = System.currentTimeMillis()
        }
    }

    /**
     * Obtener usuarios del cache
     * Lista de usuarios o null si no hay cache
     */
    suspend fun getUsers(): List<DirectoryUserDto>? {
        return try {
            val prefs = context.directoryDataStore.data.first()
            val json = prefs[KEY_USERS_JSON] ?: return null

            val type = object : TypeToken<List<DirectoryUserDto>>() {}.type
            gson.fromJson<List<DirectoryUserDto>>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verificar si el cache es válido (reciente)
     * maxAgeMinutes Edad máxima del cache en minutos (por defecto 60 min = 1 hora)
     * true si el cache es válido, false si es muy antiguo o no existe
     */
    suspend fun isCacheValid(maxAgeMinutes: Int = 60): Boolean {
        return try {
            val prefs = context.directoryDataStore.data.first()
            val lastUpdate = prefs[KEY_LAST_UPDATE] ?: return false

            val currentTime = System.currentTimeMillis()
            val ageMinutes = (currentTime - lastUpdate) / 60_000 // Convertir a minutos

            ageMinutes < maxAgeMinutes
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Limpiar cache del directorio
     */
    suspend fun clearCache() {
        context.directoryDataStore.edit { prefs ->
            prefs.remove(KEY_USERS_JSON)
            prefs.remove(KEY_LAST_UPDATE)
        }
    }
}