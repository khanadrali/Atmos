package com.abbaskhan.data.repository

import com.abbaskhan.data.local.FavoritesLocalDataSource
import com.abbaskhan.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesRepositoryImpl(
    private val localDataSource: FavoritesLocalDataSource
) : FavoritesRepository {
    override suspend fun getFavoriteCities(): List<String> {
        return localDataSource.getFavorites()
    }

    override fun observeFavoriteCities(): Flow<List<String>> {
        return localDataSource.favoritesFlow
    }

    override suspend fun addFavorite(cityName: String) {
        localDataSource.addFavorite(cityName)
    }

    override suspend fun removeFavorite(cityName: String) {
        localDataSource.removeFavorite(cityName)
    }

    override suspend fun isFavorite(cityName: String): Boolean {
        return localDataSource.isFavorite(cityName)
    }

    override suspend fun getRecentSearches(): List<String> {
        return localDataSource.getRecentSearches()
    }

    override suspend fun addRecentSearch(cityName: String) {
        localDataSource.addRecentSearch(cityName)
    }
}
