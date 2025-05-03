package com.hermen.ass1.ui.theme

import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.hermen.ass1.User.User
import kotlinx.coroutines.tasks.await
import com.hermen.ass1.User.toUser

object UserRepository {
    private val db = FirebaseFirestore.getInstance()

    // 获取所有用户
    suspend fun getUsers(): List<User> {
        return try {
            val querySnapshot = db.collection("users").get().await() // 获取 "users" 集合的所有文档
            querySnapshot.documents.mapNotNull { it.toUser() } // 使用 toUser() 将每个文档转换为 User 对象
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Failed to fetch users: ${e.message}")
            emptyList() // 如果失败返回空列表
        }
    }

    // 根据 email 获取单个用户
    suspend fun getUserByEmail(email: String): User? {
        return try {
            val documentSnapshot = db.collection("users").whereEqualTo("email", email).get().await()
            if (!documentSnapshot.isEmpty) {
                documentSnapshot.documents.first().toUser() // 将第一个文档转换为 User 对象
            } else {
                null // 如果没有找到匹配的用户，返回 null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Failed to fetch user by email: ${e.message}")
            null
        }
    }
}