package com.genialeast.mviarchitecture.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.genialeast.mviarchitecture.core.data.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "news_session")

/**
 * 会话本地数据源。登录结果写入 DataStore，冷启动时可以恢复登录态。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class SessionLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.sessionDataStore

    suspend fun readSession(): UserSession? {
        val prefs = dataStore.data.first()
        if (prefs[Keys.LOGGED_IN] != true) return null
        val username = prefs[Keys.USERNAME].orEmpty()
        if (username.isBlank()) return null
        return UserSession(
            username = username,
            nickname = prefs[Keys.NICKNAME].orEmpty(),
            role = prefs[Keys.ROLE].orEmpty(),
        )
    }

    suspend fun save(session: UserSession) {
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            prefs[Keys.USERNAME] = session.username
            prefs[Keys.NICKNAME] = session.nickname
            prefs[Keys.ROLE] = session.role
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private object Keys {
        val LOGGED_IN: Preferences.Key<Boolean> = booleanPreferencesKey("logged_in")
        val USERNAME: Preferences.Key<String> = stringPreferencesKey("username")
        val NICKNAME: Preferences.Key<String> = stringPreferencesKey("nickname")
        val ROLE: Preferences.Key<String> = stringPreferencesKey("role")
    }
}
