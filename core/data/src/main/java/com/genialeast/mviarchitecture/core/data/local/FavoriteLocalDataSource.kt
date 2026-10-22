package com.genialeast.mviarchitecture.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.favoriteDataStore by preferencesDataStore(name = "news_favorite")

/**
 * 收藏的本地数据源。保存资讯 id 集合，和会话分开存放。
 *
 * @author GenialSir
 * @date 2026-10-22
 */
@Singleton
class FavoriteLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.favoriteDataStore

    fun observeIds(): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.IDS] ?: emptySet()
    }

    suspend fun toggle(id: String) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.IDS] ?: emptySet()
            prefs[Keys.IDS] = if (id in current) current - id else current + id
        }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(Keys.IDS) }
    }

    private object Keys {
        val IDS = stringSetPreferencesKey("ids")
    }
}
