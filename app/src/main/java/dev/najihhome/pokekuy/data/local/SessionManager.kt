package dev.najihhome.pokekuy.data.local

import android.content.Context
import android.content.SharedPreferences
import dev.najihhome.pokekuy.domain.model.User

class SessionManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "pokekuy_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_CREATED_AT = "created_at"
    }
    
    fun saveUserSession(user: User) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_EMAIL, user.email)
        editor.putLong(KEY_CREATED_AT, user.createdAt)
        editor.apply()
    }
    
    fun getCurrentUser(): User? {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) {
            return null
        }
        
        val id = sharedPreferences.getLong(KEY_USER_ID, -1L)
        val username = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        val email = sharedPreferences.getString(KEY_EMAIL, "") ?: ""
        val createdAt = sharedPreferences.getLong(KEY_CREATED_AT, 0L)
        
        return User(id, username, email, createdAt)
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
