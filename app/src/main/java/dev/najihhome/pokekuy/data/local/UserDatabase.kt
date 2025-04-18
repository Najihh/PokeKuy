package dev.najihhome.pokekuy.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.najihhome.pokekuy.domain.model.User

class UserDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "pokekuy_users.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun loginUser(username: String, password: String): User? {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_CREATED_AT),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val retrievedUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            
            user = User(id, retrievedUsername, email, createdAt)
        }
        
        cursor.close()
        db.close()
        return user
    }

    fun getUserById(id: Long): User? {
        val db = readableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_CREATED_AT),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            
            user = User(userId, username, email, createdAt)
        }
        
        cursor.close()
        db.close()
        return user
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}
