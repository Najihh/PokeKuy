package dev.najihhome.pokekuy.domain.repository

import dev.najihhome.pokekuy.domain.model.User

interface UserRepository {
    suspend fun registerUser(username: String, email: String, password: String): Result<User>
    suspend fun loginUser(username: String, password: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logoutUser(): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
}
