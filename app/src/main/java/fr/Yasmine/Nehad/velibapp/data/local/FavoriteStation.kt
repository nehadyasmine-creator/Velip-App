package fr.Yasmine.Nehad.velibapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStation(
    @PrimaryKey
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val bikesAvailable: Int,
    val docksAvailable: Int,
    val ebikes: Int,
    val mechanicalBikes: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)