package dev.najihhome.pokekuy.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: Long
) {
    fun getFormattedCreatedAt(): String {
        val date = Date(createdAt)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
}
