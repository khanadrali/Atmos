package com.abbaskhan.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun getFavoriteCities(): List<String>
    fun observeFavoriteCities(): Flow<List<String>>
    suspend fun addFavorite(cityName: String)
    suspend fun removeFavorite(cityName: String)
    suspend fun isFavorite(cityName: String): Boolean
    suspend fun getRecentSearches(): List<String>
    suspend fun addRecentSearch(cityName: String)
}
