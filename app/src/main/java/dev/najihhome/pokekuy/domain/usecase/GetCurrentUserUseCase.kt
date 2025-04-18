package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.User
import dev.najihhome.pokekuy.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrentUserUseCase(private val userRepository: UserRepository) {
    
    suspend operator fun invoke(): Result<User> = withContext(Dispatchers.IO) {
        return@withContext userRepository.getCurrentUser()
    }
    
    suspend fun isUserLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        return@withContext userRepository.isUserLoggedIn()
    }
    
    suspend fun logoutUser(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext userRepository.logoutUser()
    }
}
