package com.ahmed.cinema.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

data class RecentlyViewedMovie(
    val id: Int,
    val title: String,
    val posterPath: String,
    val timestamp: Long = System.currentTimeMillis()
)

val Context.recentlyViewedDataStore by preferencesDataStore(name = "recently_viewed")

object RecentlyViewedManager {
    private val RECENTLY_VIEWED_KEY = stringPreferencesKey("recently_viewed_movies")
    
    fun getRecentlyViewedFlow(context: Context): Flow<List<RecentlyViewedMovie>> {
        return context.recentlyViewedDataStore.data.map { preferences ->
            val json = preferences[RECENTLY_VIEWED_KEY] ?: "[]"
            try {
                val jsonArray = JSONArray(json)
                val movies = mutableListOf<RecentlyViewedMovie>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    movies.add(
                        RecentlyViewedMovie(
                            id = obj.getInt("id"),
                            title = obj.getString("title"),
                            posterPath = obj.getString("posterPath"),
                            timestamp = obj.getLong("timestamp")
                        )
                    )
                }
                movies.sortedByDescending { it.timestamp }.take(5)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun addRecentlyViewed(
        context: Context,
        movieId: Int,
        title: String,
        posterPath: String
    ) {
        context.recentlyViewedDataStore.edit { preferences ->
            val json = preferences[RECENTLY_VIEWED_KEY] ?: "[]"
            val jsonArray = try {
                JSONArray(json)
            } catch (e: Exception) {
                JSONArray()
            }
            
            // Convert to mutable list
            val movies = mutableListOf<RecentlyViewedMovie>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                movies.add(
                    RecentlyViewedMovie(
                        id = obj.getInt("id"),
                        title = obj.getString("title"),
                        posterPath = obj.getString("posterPath"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
            
            // Remove if already exists, then add to front
            movies.removeAll { it.id == movieId }
            movies.add(0, RecentlyViewedMovie(movieId, title, posterPath))
            
            // Keep only last 5
            val updated = movies.take(5)
            
            // Convert back to JSON
            val resultArray = JSONArray()
            for (movie in updated) {
                val obj = JSONObject()
                obj.put("id", movie.id)
                obj.put("title", movie.title)
                obj.put("posterPath", movie.posterPath)
                obj.put("timestamp", movie.timestamp)
                resultArray.put(obj)
            }
            
            preferences[RECENTLY_VIEWED_KEY] = resultArray.toString()
        }
    }
}
