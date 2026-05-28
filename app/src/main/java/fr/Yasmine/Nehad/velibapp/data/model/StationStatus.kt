package fr.Yasmine.Nehad.velibapp.data.model

data class StationStatusResponse(
    val data: StationStatusData,
    val lastUpdatedOther: Long,
    val ttl: Int
)

data class StationStatusData(
    val stations: List<StationStatus>
)

data class StationStatus(
    val station_id: Long,
    val numBikesAvailable: Int,
    val numDocksAvailable: Int,
    val is_installed: Int,
    val is_renting: Int,
    val is_returning: Int,
    val last_reported: Long,
    val num_bikes_available_types: List<Map<String, Int>>
)
