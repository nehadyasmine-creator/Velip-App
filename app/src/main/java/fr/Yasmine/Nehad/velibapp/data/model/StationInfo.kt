package fr.Yasmine.Nehad.velibapp.data.model

data class StationInfoResponse(
    val data: StationInfoData,
    val lastUpdatedOther: Long,
    val ttl: Int
)

data class StationInfoData(
    val stations: List<StationInfo>
)

data class StationInfo(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int
)