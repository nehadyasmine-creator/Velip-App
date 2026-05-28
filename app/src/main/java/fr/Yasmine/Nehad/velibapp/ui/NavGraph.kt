package fr.Yasmine.Nehad.velibapp.ui

import fr.Yasmine.Nehad.velibapp.ui.details.DetailScreen
import fr.Yasmine.Nehad.velibapp.ui.favorites.FavoritesScreen
import fr.Yasmine.Nehad.velibapp.ui.map.MapScreen
import fr.Yasmine.Nehad.velibapp.ui.nearby.NearbyScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val SPLASH = "splash"
    const val MAP = "map"
    const val DETAIL = "detail"
    const val FAVORITES = "favorites"
    const val NEARBY = "nearby"
}

@Composable
fun VelibNavGraph(
    viewModel: VelibViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = viewModel,
                onStationClick = { station ->
                    viewModel.selectStation(station)
                    navController.navigate(Routes.DETAIL)
                },
                onFavoritesClick = {
                    navController.navigate(Routes.FAVORITES)
                },
                onNearbyClick = {
                    navController.navigate(Routes.NEARBY)
                }
            )
        }

        composable(Routes.DETAIL) {
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                viewModel = viewModel,
                onStationClick = { station ->
                    viewModel.selectStation(station)
                    navController.navigate(Routes.DETAIL)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NEARBY) {
            NearbyScreen(
                viewModel = viewModel,
                onStationClick = { station ->
                    viewModel.selectStation(station)
                    navController.navigate(Routes.DETAIL)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}