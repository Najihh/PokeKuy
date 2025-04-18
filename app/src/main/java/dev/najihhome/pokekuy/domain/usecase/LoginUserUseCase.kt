package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.User
import dev.najihhome.pokekuy.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUserUseCase(private val userRepository: UserRepository) {
    
    suspend operator fun invoke(username: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        when {
            username.isBlank() -> {
                return@withContext Result.failure(Exception("Username cannot be empty"))
            }
            password.isBlank() -> {
                return@withContext Result.failure(Exception("Password cannot be empty"))
            }
            else -> {
                return@withContext userRepository.loginUser(username, password)
            }
        }
    }
}
