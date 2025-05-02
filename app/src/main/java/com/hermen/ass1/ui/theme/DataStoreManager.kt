package com.hermen.ass1.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

object DataStoreManager {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    // 获取 DataStore
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    // 设置登录状态
    suspend fun setLoggedIn(context: Context, loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
        }
    }

    // 获取登录状态
    fun getLoggedIn(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }
    }

}
