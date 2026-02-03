package com.example.foroapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class PostPreferences(private val context: Context) {

    companion object {
        private val LIKED_POSTS_KEY = stringSetPreferencesKey("liked_posts")
    }

    // Obtener el flujo de IDs de posts con "Like"
    val likedPosts: Flow<Set<Long>> = context.dataStore.data.map { preferences ->
        preferences[LIKED_POSTS_KEY]?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()
    }

    // Guardar el conjunto de IDs
    suspend fun saveLikedPosts(likedIds: Set<Long>) {
        context.dataStore.edit { preferences ->
            preferences[LIKED_POSTS_KEY] = likedIds.map { it.toString() }.toSet()
        }
    }
}
