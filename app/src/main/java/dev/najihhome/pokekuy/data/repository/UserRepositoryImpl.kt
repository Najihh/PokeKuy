package dev.najihhome.pokekuy.data.repository

import dev.najihhome.pokekuy.data.local.SessionManager
import dev.najihhome.pokekuy.data.local.UserDatabase
import dev.najihhome.pokekuy.domain.model.User
import dev.najihhome.pokekuy.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDatabase: UserDatabase,
    private val sessionManager: SessionManager
) : UserRepository {
    
    override suspend fun registerUser(
        username: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            if (userDatabase.isUsernameExists(username)) {
                return Result.failure(Exception("Username already exists"))
            }
            
            if (userDatabase.isEmailExists(email)) {
                return Result.failure(Exception("Email already exists"))
            }

            val userId = userDatabase.registerUser(username, email, password)
            
            if (userId == -1L) {
                Result.failure(Exception("Failed to register user"))
            } else {
                val user = User(userId, username, email, System.currentTimeMillis())
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }

    override suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = userDatabase.loginUser(username, password)
            if (user != null) {
                sessionManager.saveUserSession(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val user = sessionManager.getCurrentUser()
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get current user: ${e.message}"))
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        return try {
            sessionManager.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Logout failed: ${e.message}"))
        }
    }
    
    override suspend fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
}
