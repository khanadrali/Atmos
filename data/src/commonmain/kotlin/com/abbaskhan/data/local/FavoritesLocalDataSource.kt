package com.abbaskhan.data.local

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesLocalDataSource(private val settings: Settings) {
    private val favoritesKey = "favorite_cities_key"
    private val recentSearchesKey = "recent_searches_key"

    private val _favoritesFlow = MutableStateFlow(getFavorites())
    val favoritesFlow: Flow<List<String>> = _favoritesFlow.asStateFlow()

    fun getFavorites(): List<String> {
        val savedStr = settings.getString(favoritesKey, "")
        if (savedStr.isBlank()) return emptyList()
        return savedStr.split(",").filter { it.isNotBlank() }
    }

    fun addFavorite(cityName: String) {
        val current = getFavorites().toMutableSet()
        current.add(cityName)
        save(current)
    }

    fun removeFavorite(cityName: String) {
        val current = getFavorites().toMutableSet()
        current.remove(cityName)
        save(current)
    }

    fun isFavorite(cityName: String): Boolean {
        return getFavorites().contains(cityName)
    }

    private fun save(cities: Set<String>) {
        val str = cities.joinToString(",")
        settings.putString(favoritesKey, str)
        _favoritesFlow.value = cities.toList()
    }

    fun getRecentSearches(): List<String> {
        val savedStr = settings.getString(recentSearchesKey, "")
        if (savedStr.isBlank()) return emptyList()
        return savedStr.split(",").filter { it.isNotBlank() }
    }

    fun addRecentSearch(cityName: String) {
        if (cityName.isBlank()) return
        val current = getRecentSearches().toMutableList()
        current.remove(cityName)
        current.add(0, cityName)
        val limited = current.take(5)
        settings.putString(recentSearchesKey, limited.joinToString(","))
    }
}
