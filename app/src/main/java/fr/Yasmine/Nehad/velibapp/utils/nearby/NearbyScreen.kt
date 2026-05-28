package fr.Yasmine.Nehad.velibapp.ui.nearby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.Yasmine.Nehad.velibapp.data.model.Station
import fr.Yasmine.Nehad.velibapp.ui.VelibViewModel
import fr.Yasmine.Nehad.velibapp.utils.distanceBetween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyScreen(
    viewModel: VelibViewModel,
    onStationClick: (Station) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val nearbyStations by viewModel.nearbyStations.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserLocation(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stations proches", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour",
                            tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Slider rayon
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Rayon de recherche : ${searchRadius}m",
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = searchRadius.toFloat(),
                        onValueChange = { viewModel.setSearchRadius(it.toInt()) },
                        valueRange = 100f..2000f,
                        steps = 18,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("100m", color = Color.Gray, fontSize = 12.sp)
                        Text("2000m", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (userLocation == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Localisation en cours...", color = Color.Gray)
                    }
                }
            } else if (nearbyStations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aucune station dans ce rayon", color = Color.Gray)
                        Text("Augmente le rayon de recherche", color = Color.Gray)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "📍 ${nearbyStations.size} stations trouvées",
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(nearbyStations) { station ->
                        NearbyStationCard(
                            station = station,
                            distance = userLocation?.let {
                                distanceBetween(
                                    it.latitude, it.longitude,
                                    station.lat, station.lon
                                )
                            } ?: 0f,
                            onClick = { onStationClick(station) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NearbyStationCard(
    station: Station,
    distance: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("🚲", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(station.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🚲 ${station.bikesAvailable}", color = Color.Gray, fontSize = 13.sp)
                    Text("🅿️ ${station.docksAvailable}", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (distance < 1000) "${distance.toInt()}m"
                    else "${"%.1f".format(distance / 1000)}km",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }
    }
}