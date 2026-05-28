package fr.Yasmine.Nehad.velibapp.repository

import fr.Yasmine.Nehad.velibapp.data.api.VelibApiService
import fr.Yasmine.Nehad.velibapp.data.local.FavoriteStation
import fr.Yasmine.Nehad.velibapp.data.local.FavoriteStationDao
import fr.Yasmine.Nehad.velibapp.data.model.Station
import kotlinx.coroutines.flow.Flow

class VelibRepository(
    private val apiService: VelibApiService,
    private val dao: FavoriteStationDao
) {

    // --- API ---
    suspend fun getStations(): List<Station> {
        val infoList = apiService.getStationInformation().data.stations
        val statusList = apiService.getStationStatus().data.stations

        val statusMap = statusList.associateBy { it.station_id }

        return infoList.mapNotNull { info ->
            val status = statusMap[info.station_id] ?: return@mapNotNull null

            // ✅ Nouveau calcul correct
            val bikeTypes = status.num_bikes_available_types
            val ebikes = bikeTypes.sumOf { it["ebike"] ?: 0 }
            val mechanical = bikeTypes.sumOf { it["mechanical"] ?: 0 }
            val realCapacity = ebikes + mechanical + status.numDocksAvailable

            Station(
                id = info.station_id,
                name = info.name,
                lat = info.lat,
                lon = info.lon,
                capacity = realCapacity,
                bikesAvailable = ebikes + mechanical,
                docksAvailable = status.numDocksAvailable,
                ebikes = ebikes,
                mechanicalBikes = mechanical,
                isInstalled = status.is_installed == 1,
                isRenting = status.is_renting == 1
            )
        }
    }

    // --- Favoris ---
    fun getAllFavorites(): Flow<List<FavoriteStation>> = dao.getAllFavorites()

    fun isFavorite(stationId: Long): Flow<Boolean> = dao.isFavorite(stationId)

    suspend fun addFavorite(station: Station) {
        dao.insertFavorite(
            FavoriteStation(
                id = station.id,
                name = station.name,
                lat = station.lat,
                lon = station.lon,
                capacity = station.capacity,
                bikesAvailable = station.bikesAvailable,
                docksAvailable = station.docksAvailable,
                ebikes = station.ebikes,
                mechanicalBikes = station.mechanicalBikes
            )
        )
    }


    suspend fun removeFavorite(station: Station) {
        dao.deleteFavorite(
            FavoriteStation(
                id = station.id,
                name = station.name,
                lat = station.lat,
                lon = station.lon,
                capacity = station.capacity,
                bikesAvailable = station.bikesAvailable,
                docksAvailable = station.docksAvailable,
                ebikes = station.ebikes,
                mechanicalBikes = station.mechanicalBikes
            )
        )
    }
}