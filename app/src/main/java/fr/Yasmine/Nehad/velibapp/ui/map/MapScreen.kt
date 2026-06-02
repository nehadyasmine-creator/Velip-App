package fr.Yasmine.Nehad.velibapp.ui.map

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.preference.PreferenceManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import fr.Yasmine.Nehad.velibapp.data.model.Station
import fr.Yasmine.Nehad.velibapp.ui.VelibViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

fun createPinDrawable(context: Context): Drawable {
    val width = 60
    val height = 90
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

    paint.color = android.graphics.Color.rgb(46, 125, 50)
    canvas.drawCircle(width / 2f, width / 2f, width / 2f, paint)

    val path = android.graphics.Path().apply {
        moveTo(width / 2f - 15f, width / 2f + 20f)
        lineTo(width / 2f + 15f, width / 2f + 20f)
        lineTo(width / 2f, height.toFloat())
        close()
    }
    canvas.drawPath(path, paint)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(width / 2f, width / 2f, width / 5f, paint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

object MapState {
    var savedCenter: GeoPoint = GeoPoint(48.8566, 2.3522)
    var savedZoom: Double = 13.0
    var hasZoomedOnUser: Boolean = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: VelibViewModel,
    onStationClick: (Station) -> Unit,
    onFavoritesClick: () -> Unit,
    onNearbyClick: () -> Unit
) {
    val context = LocalContext.current
    val stations by viewModel.filteredStations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    var myLocationOverlay: MyLocationNewOverlay? = null
    var mapViewRef: MapView? = null

    LaunchedEffect(Unit) {
        viewModel.initLocation(context)
    }

    LaunchedEffect(userLocation) {
        if (userLocation != null && !MapState.hasZoomedOnUser) {
            val userGeoPoint = GeoPoint(userLocation!!.latitude, userLocation!!.longitude)
            mapViewRef?.controller?.animateTo(userGeoPoint)
            mapViewRef?.controller?.setZoom(12.0)
            MapState.savedCenter = userGeoPoint
            MapState.savedZoom = 12.0
            MapState.hasZoomedOnUser = true
        }
    }

    Configuration.getInstance().load(
        context,
        PreferenceManager.getDefaultSharedPreferences(context)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚲", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Vélib'App", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadStations() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        controller.setZoom(MapState.savedZoom)
                        controller.setCenter(MapState.savedCenter)

                        val locationOverlay = MyLocationNewOverlay(
                            GpsMyLocationProvider(ctx), this
                        )
                        locationOverlay.enableMyLocation()
                        overlays.add(locationOverlay)
                        myLocationOverlay = locationOverlay
                        mapViewRef = this

                        addMapListener(object : org.osmdroid.events.MapListener {
                            override fun onScroll(event: org.osmdroid.events.ScrollEvent): Boolean {
                                MapState.savedCenter = mapCenter as GeoPoint
                                MapState.savedZoom = zoomLevelDouble
                                return false
                            }
                            override fun onZoom(event: org.osmdroid.events.ZoomEvent): Boolean {
                                MapState.savedCenter = mapCenter as GeoPoint
                                MapState.savedZoom = zoomLevelDouble
                                return false
                            }
                        })
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it !is MyLocationNewOverlay }
                    stations.forEach { station ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(station.lat, station.lon)
                            title = station.name
                            snippet = "🚲 ${station.bikesAvailable} vélos | 🅿️ ${station.docksAvailable} places"
                            icon = createPinDrawable(mapView.context)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                onStationClick(station)
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = onFavoritesClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favoris", tint = Color.White)
                }
                FloatingActionButton(
                    onClick = onNearbyClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.List, contentDescription = "Stations proches", tint = Color.White)
                }
                FloatingActionButton(
                    onClick = {
                        myLocationOverlay?.myLocation?.let { location ->
                            mapViewRef?.controller?.animateTo(location)
                            mapViewRef?.controller?.setZoom(15.0)
                            MapState.savedCenter = location
                            MapState.savedZoom = 15.0
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Ma position", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    FilterChip(
                        selected = activeFilter == VelibViewModel.StationFilter.ALL,
                        onClick = { viewModel.setFilter(VelibViewModel.StationFilter.ALL) },
                        label = { Text("Tout") }
                    )
                    FilterChip(
                        selected = activeFilter == VelibViewModel.StationFilter.BIKES_AVAILABLE,
                        onClick = { viewModel.setFilter(VelibViewModel.StationFilter.BIKES_AVAILABLE) },
                        label = { Text("🚲 Dispo") }
                    )
                    FilterChip(
                        selected = activeFilter == VelibViewModel.StationFilter.ELECTRIC_ONLY,
                        onClick = { viewModel.setFilter(VelibViewModel.StationFilter.ELECTRIC_ONLY) },
                        label = { Text("⚡ Électrique") }
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Rechercher une station...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error?.let {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}