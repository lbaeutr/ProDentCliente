package dev.luisbaena.prodentclient.domain.usecase.user

import android.util.Patterns
import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
    * UseCase para el inicio de sesión de un usuario
    */
class LoginUseCase @Inject constructor(
    private  val authRepository: AuthRepository
){
    suspend operator fun invoke(email:String, password:String): Result<User>
    {
        if (email.isBlank()){
            return Result.failure(Exception("El email no puede estar vacio"))
        }
        if (password.length < 6){
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (!isValidEmail(email)){
            return Result.failure(Exception("El formato del email es incorrecto"))
        }

        return try {
            val result = authRepository.login(email.trim().lowercase(), password)
            if (result.isSuccess){
                // Guardar la sesion del usuario AUTOMATICAMENTE
                result.getOrNull()?.let {
                    user ->
                    authRepository.saveUserSession(user)
                }
            }
            result
        } catch (e: Exception){
            Result.failure(Exception("Error durante la conexion: ${e.message}"))
        }
    }

 // La función isValidEmail verifica si el parámetro recibido es un email válido usando Patterns.EMAIL_ADDRESS, una expresión regular predefinida en Android. Retorna true si el formato es correcto, false si no lo es.
    private fun isValidEmail(email: String): Boolean {
       return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}