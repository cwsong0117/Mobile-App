package com.hermen.ass1.User

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val age: Int = 0,
    val contactNo: String = "",
    val department: String = "",
    val email: String = "",
    val name: String = "",
    val position: String = "",
    val password: String = "" // 💥 Added password field
)

fun DocumentSnapshot.toUser(): User {
    val age = getLong("age")?.toInt() ?: 0
    val rawContactNo = getString("contactNo") ?: ""
    val formattedContact = formatContactNumber(rawContactNo)

    val department = getString("department") ?: ""
    val email = getString("email") ?: ""
    val name = getString("name") ?: ""
    val position = getString("position") ?: ""
    val password = getString("password") ?: "" // 💥 Extract password from Firestore

    return User(
        id = this.id,
        age = age,
        contactNo = formattedContact,
        department = department,
        email = email,
        name = name,
        position = position,
        password = password // 💥 Set password here too

    )
}

fun formatContactNumber(number: String): String {
    return when (number.length) {
        10 -> "${number.substring(0, 3)}-${number.substring(3, 6)} ${number.substring(6)}"   // 019-876 5432
        11 -> "${number.substring(0, 3)}-${number.substring(3, 7)} ${number.substring(7)}"   // 019-8765 4321
        else -> number // fallback to original if unexpected length
    }
}