package dev.luisbaena.prodentclient.data.local.preferencias


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import dev.luisbaena.prodentclient.domain.model.User

/**
 * En este archivo se maneja el almacenamiento local de las preferencias del usuario
    * utilizando DataStore de Android.
    * Defino diferentes  funciones para guardar, obtener y borrar las preferencias del usuario.
    * Esta clase es inyectada  en otras partes de la aplicación usando Hilt.
    * El archivo de preferencias se llama "prodent_user_prefs".
    * Las funciones que encontramos son:
        - saveUser: Guarda los datos del usuario en las preferencias.
        - getUser: Obtiene los datos del usuario de las preferencias.
        - clearUser: Borra todas las preferencias del usuario.
        - isLoggedIn: Verifica si el usuario está logueado.
        - getUserToken: Obtiene el token de autenticación del usuario.
        - getUserFlow: Proporciona un flujo reactivo de los datos del usuario.
    * Las claves usadas para almacenar los datos son definidas como constantes en el companion object.
 */


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prodent_user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    // Claves para las preferencias almacenadas
    companion object {
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_LASTNAME = stringPreferencesKey("user_lastname")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        private val KEY_USER_TOKEN = stringPreferencesKey("user_token")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    }

    // Guarda los datos del usuario en las preferencias
    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = user.nombre
            prefs[KEY_USER_LASTNAME] = user.apellido
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_PHONE] = user.telefono
            prefs[KEY_USER_TOKEN] = user.token
            prefs[KEY_USER_ROLE] = user.role
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    // Obtiene los datos del usuario de las preferencias
    suspend fun getUser(): User?{
        val preferences = context.dataStore.data.first()

        val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
        if (!isLoggedIn)  return null

        val email = preferences[KEY_USER_EMAIL] ?: return null
        val phone = preferences[KEY_USER_PHONE] ?: return null
        val name = preferences[KEY_USER_NAME] ?: return null
        val lastname = preferences[KEY_USER_LASTNAME] ?: return null
        val token = preferences[KEY_USER_TOKEN] ?: return null
        val role = preferences[KEY_USER_ROLE] ?: return null

        if (email.isBlank() || token.isBlank()) {
            clearUser()
            return null
        }

        return User(
            id = "",
            nombre = name,
            apellido = lastname,
            email = email,
            telefono = phone,
            token = token,
            role = role
        )
    }

    // Borra todas las preferencias del usuario
    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // Verifica si el usuario está logueado
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_IS_LOGGED_IN] ?: false
    }

    // Obtiene el token de autenticación del usuario
    suspend fun getUserToken(): String? {
        val preferences = context.dataStore.data.first()
        return if (preferences[KEY_IS_LOGGED_IN] == true) {
            preferences[KEY_USER_TOKEN]
        } else  null
    }

    // Proporciona un flujo reactivo de los datos del usuario, funciona de la siguiente manera:
    // Si el usuario no está logueado, emite null.
    // Si falta algún dato esencial del usuario, emite null.
    // Si todos los datos están presentes, emite un objeto User con los datos almacenados
    fun getUserFlow() = context.dataStore.data.map { prefs ->
        val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
        if (!isLoggedIn)  return@map null

        val email = prefs[KEY_USER_EMAIL] ?: return@map null
        val phone = prefs[KEY_USER_PHONE] ?: return@map null
        val name = prefs[KEY_USER_NAME] ?: return@map null
        val lastname = prefs[KEY_USER_LASTNAME] ?: return@map null
        val token = prefs[KEY_USER_TOKEN] ?: return@map null
        val role = prefs[KEY_USER_ROLE] ?: return@map null

        if (email.isBlank() || token.isBlank()) return@map null

        User("", name, lastname, email, phone, token, role)
    }
}