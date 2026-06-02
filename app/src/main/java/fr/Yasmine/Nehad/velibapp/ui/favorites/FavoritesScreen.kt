package fr.Yasmine.Nehad.velibapp.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.Yasmine.Nehad.velibapp.data.local.FavoriteStation
import fr.Yasmine.Nehad.velibapp.data.model.Station
import fr.Yasmine.Nehad.velibapp.ui.VelibViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: VelibViewModel,
    onStationClick: (Station) -> Unit,
    onBack: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mes favoris", fontWeight = FontWeight.Bold)
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
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Aucun favori pour l'instant",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Appuie sur ❤️ dans le détail d'une station",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(favorites) { favorite ->
                    FavoriteCard(
                        favorite = favorite,
                        onClick = { onStationClick(favorite.toStation()) },
                        onDelete = { viewModel.toggleFavorite(favorite.toStation()) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(
    favorite: FavoriteStation,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
                Text(
                    text = favorite.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🚲 ${favorite.bikesAvailable} vélos",
                        color = Color.Gray, fontSize = 13.sp)
                    Text("🅿️ ${favorite.docksAvailable} places",
                        color = Color.Gray, fontSize = 13.sp)
                }
                Text(
                    text = "⚠️ Données peut-être non à jour",
                    color = Color(0xFFFF9800),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer",
                    tint = Color.Red)
            }
        }
    }
}

fun FavoriteStation.toStation() = Station(
    id = id,
    name = name,
    lat = lat,
    lon = lon,
    capacity = capacity,
    bikesAvailable = bikesAvailable,
    docksAvailable = docksAvailable,
    ebikes = ebikes,
    mechanicalBikes = mechanicalBikes,
    isInstalled = true,
    isRenting = true,
    isFavorite = true
)