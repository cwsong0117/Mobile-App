package com.hermen.ass1.User

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val age: Int = 0,
    val contactNo: String = "",
    val department: String = "",
    val email: String = "",
    val name: String = "",
    val position: String = ""
)

fun DocumentSnapshot.toUser(): User {
    val age = getLong("age")?.toInt() ?: 0
    val rawContactNo = getString("contactNo") ?: ""
    val formattedContact = formatContactNumber(rawContactNo)

    val department = getString("department") ?: ""
    val email = getString("email") ?: ""
    val name = getString("name") ?: ""
    val position = getString("position") ?: ""

    return User(
        age = age,
        contactNo = formattedContact,
        department = department,
        email = email,
        name = name,
        position = position
    )
}

fun formatContactNumber(number: String): String {
    return when (number.length) {
        10 -> "${number.substring(0, 3)}-${number.substring(3, 6)} ${number.substring(6)}"   // 019-876 5432
        11 -> "${number.substring(0, 3)}-${number.substring(3, 7)} ${number.substring(7)}"   // 019-8765 4321
        else -> number // fallback to original if unexpected length
    }
}
