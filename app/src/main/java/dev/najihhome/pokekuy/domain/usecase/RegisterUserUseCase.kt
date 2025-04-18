package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.User
import dev.najihhome.pokekuy.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterUserUseCase(private val userRepository: UserRepository) {
    
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> = withContext(Dispatchers.IO) {
        when {
            username.isBlank() -> {
                return@withContext Result.failure(Exception("Username cannot be empty"))
            }
            username.length < 4 -> {
                return@withContext Result.failure(Exception("Username must be at least 4 characters"))
            }
            email.isBlank() -> {
                return@withContext Result.failure(Exception("Email cannot be empty"))
            }
            !isValidEmail(email) -> {
                return@withContext Result.failure(Exception("Invalid email format"))
            }
            password.isBlank() -> {
                return@withContext Result.failure(Exception("Password cannot be empty"))
            }
            password.length < 6 -> {
                return@withContext Result.failure(Exception("Password must be at least 6 characters"))
            }
            password != confirmPassword -> {
                return@withContext Result.failure(Exception("Passwords do not match"))
            }
            else -> {
                return@withContext userRepository.registerUser(username, email, password)
            }
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }
}
