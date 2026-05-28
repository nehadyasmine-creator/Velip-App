package fr.Yasmine.Nehad.velibapp.data.api
import fr.Yasmine.Nehad.velibapp.data.model.StationInfoResponse
import fr.Yasmine.Nehad.velibapp.data.model.StationStatusResponse

import retrofit2.http.GET

interface VelibApiService {

    @GET("station_information.json")
    suspend fun getStationInformation(): StationInfoResponse

    @GET("station_status.json")
    suspend fun getStationStatus(): StationStatusResponse
}