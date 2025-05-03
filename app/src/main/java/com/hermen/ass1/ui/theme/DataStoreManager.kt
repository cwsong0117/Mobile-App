package com.hermen.ass1.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.hermen.ass1.User.SessionManager
import com.hermen.ass1.User.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


// 获取 DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreManager {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_EMAIL = stringPreferencesKey("user_email")
    private val USER_OBJECT_KEY = stringPreferencesKey("user_object")

    // 设置登录状态
    suspend fun setLoggedIn(context: Context, loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
        }
    }

    suspend fun saveCurrentUser(context: Context, user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_OBJECT_KEY] = Json.encodeToString(user)
        }
    }

    suspend fun getCurrentUser(context: Context): User? {
        val prefs = context.dataStore.data.first()
        val userJson = prefs[USER_OBJECT_KEY]
        return userJson?.let {
            Json.decodeFromString<User>(it)
        }
    }

    // 获取登录状态
    fun getLoggedIn(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }
    }

    suspend fun saveUserInfo(context: Context, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
        }
    }


    // ✅ 获取用户资料
    fun getUserEmail(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USER_EMAIL] }

    fun getUserName(context: Context): Flow<String?> =
        context.dataStore.data.map { it[USER_NAME] }

}