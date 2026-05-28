package fr.Yasmine.Nehad.velibapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {

    @Query("SELECT * FROM favorite_stations")
    fun getAllFavorites(): Flow<List<FavoriteStation>>

    @Query("SELECT * FROM favorite_stations WHERE id = :stationId")
    suspend fun getFavoriteById(stationId: Long): FavoriteStation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(station: FavoriteStation)

    @Delete
    suspend fun deleteFavorite(station: FavoriteStation)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE id = :stationId)")
    fun isFavorite(stationId: Long): Flow<Boolean>
}