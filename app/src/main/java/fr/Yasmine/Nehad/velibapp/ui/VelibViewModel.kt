package fr.Yasmine.Nehad.velibapp.ui
import fr.Yasmine.Nehad.velibapp.data.api.RetrofitClient
import fr.Yasmine.Nehad.velibapp.data.local.FavoriteStation
import fr.Yasmine.Nehad.velibapp.data.local.VelibDatabase
import fr.Yasmine.Nehad.velibapp.data.model.Station
import fr.Yasmine.Nehad.velibapp.repository.VelibRepository
import kotlinx.coroutines.flow.combine

import fr.Yasmine.Nehad.velibapp.utils.getCurrentLocation
import fr.Yasmine.Nehad.velibapp.utils.distanceBetween

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VelibViewModel(application: Application) : AndroidViewModel(application) {

    private val database = VelibDatabase.getDatabase(application)
    private val repository = VelibRepository(
        apiService = RetrofitClient.apiService,
        dao = database.favoriteStationDao()
    )

    // Liste de toutes les stations
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations

    // Station sélectionnée pour le détail
    private val _selectedStation = MutableStateFlow<Station?>(null)
    val selectedStation: StateFlow<Station?> = _selectedStation

    // Favoris
    val favorites: StateFlow<List<FavoriteStation>> = repository
        .getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Chargement / erreur
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Rayon de recherche en mètres
    private val _searchRadius = MutableStateFlow(500)
    val searchRadius: StateFlow<Int> = _searchRadius

    init {
        loadStations()
    }

    fun initLocation(context: android.content.Context) {
        if (_userLocation.value == null) {
            loadUserLocation(context)
        }
    }

    fun loadStations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _stations.value = repository.getStations()
            } catch (e: Exception) {
                _error.value = "Erreur de chargement : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectStation(station: Station) {
        _selectedStation.value = station
    }

    fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(station.id).first()
            if (isFav) {
                repository.removeFavorite(station)
            } else {
                repository.addFavorite(station)
            }
        }
    }

    fun isFavorite(stationId: Long): Flow<Boolean> = repository.isFavorite(stationId)

    private val _userLocation = MutableStateFlow<android.location.Location?>(null)
    val userLocation: StateFlow<android.location.Location?> = _userLocation

    private val _nearbyStations = MutableStateFlow<List<Station>>(emptyList())
    val nearbyStations: StateFlow<List<Station>> = _nearbyStations

    fun loadUserLocation(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val location = getCurrentLocation(context)
                _userLocation.value = location
                updateNearbyStations()
            } catch (e: Exception) {
                _error.value = "Localisation impossible : ${e.message}"
            }
        }
    }

    fun updateNearbyStations() {
        val location = _userLocation.value ?: return
        val radius = _searchRadius.value.toFloat()
        _nearbyStations.value = _stations.value.filter { station ->
            distanceBetween(
                location.latitude, location.longitude,
                station.lat, station.lon
            ) <= radius
        }.sortedBy { station ->
            distanceBetween(
                location.latitude, location.longitude,
                station.lat, station.lon
            )
        }
    }

    fun setSearchRadius(radius: Int) {
        _searchRadius.value = radius
        updateNearbyStations()
    }

    // Recherche
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Filtre
    enum class StationFilter { ALL, BIKES_AVAILABLE, ELECTRIC_ONLY }
    private val _activeFilter = MutableStateFlow(StationFilter.ALL)
    val activeFilter: StateFlow<StationFilter> = _activeFilter

    // Stations filtrées
    val filteredStations: StateFlow<List<Station>> = combine(
        _stations, _searchQuery, _activeFilter
    ) { stations, query, filter ->
        stations
            .filter { station ->
                query.isEmpty() || station.name.contains(query, ignoreCase = true)
            }
            .filter { station ->
                when (filter) {
                    StationFilter.ALL -> true
                    StationFilter.BIKES_AVAILABLE -> station.bikesAvailable > 0
                    StationFilter.ELECTRIC_ONLY -> station.ebikes > 0
                }
            }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: StationFilter) {
        _activeFilter.value = filter
    }

    fun getDistanceToStation(station: Station): Float? {
        val location = _userLocation.value ?: return null
        return distanceBetween(
            location.latitude, location.longitude,
            station.lat, station.lon
        )
    }
}