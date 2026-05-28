package fr.Yasmine.Nehad.velibapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.Yasmine.Nehad.velibapp.ui.VelibViewModel
import fr.Yasmine.Nehad.velibapp.utils.openGoogleMaps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: VelibViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val station = viewModel.selectedStation.collectAsState().value
    val isFavorite by viewModel.isFavorite(station?.id ?: 0L).collectAsState(initial = false)
    val distance = station?.let { viewModel.getDistanceToStation(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚲", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            station?.name ?: "Détail",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { station?.let { viewModel.toggleFavorite(it) } }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) Color(0xFFFFD700) else Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (station == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune station sélectionnée")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusCard(isInstalled = station.isInstalled, isRenting = station.isRenting)

                // Distance + boutons navigation
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (distance != null) {
                            val distanceText = if (distance < 1000)
                                "${distance.toInt()} m"
                            else
                                "${"%.1f".format(distance / 1000)} km"

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍 Distance : ", fontWeight = FontWeight.Bold)
                                Text(
                                    distanceText,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { openGoogleMaps(context, station.lat, station.lon, station.name) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🗺️ Google Maps")
                            }
                        }
                    }
                }

                InfoCard(
                    title = "Vélos disponibles",
                    value = "${station.bikesAvailable}",
                    details = listOf(
                        "🔋 Électriques : ${station.ebikes}",
                        "⚙️ Mécaniques : ${station.mechanicalBikes}"
                    )
                )

                InfoCard(
                    title = "Places disponibles",
                    value = "${station.docksAvailable}",
                    details = listOf("Capacité totale : ${station.capacity}")
                )
            }
        }
    }
}

@Composable
fun StatusCard(isInstalled: Boolean, isRenting: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isInstalled && isRenting)
                Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isInstalled && isRenting) "✅ Station opérationnelle"
                else "❌ Station hors service",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, details: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            details.forEach { detail ->
                Text(detail, color = Color.Gray)
            }
        }
    }
}