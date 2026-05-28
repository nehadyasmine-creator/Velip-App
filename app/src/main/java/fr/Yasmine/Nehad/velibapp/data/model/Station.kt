package fr.Yasmine.Nehad.velibapp.data.model

data class Station(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val bikesAvailable: Int,
    val docksAvailable: Int,
    val ebikes: Int,
    val mechanicalBikes: Int,
    val isInstalled: Boolean,
    val isRenting: Boolean,
    val isFavorite: Boolean = false
)