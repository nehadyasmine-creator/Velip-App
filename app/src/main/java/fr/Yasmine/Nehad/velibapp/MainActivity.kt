package fr.Yasmine.Nehad.velibapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import fr.Yasmine.Nehad.velibapp.ui.VelibNavGraph
import fr.Yasmine.Nehad.velibapp.ui.VelibViewModel
import fr.Yasmine.Nehad.velibapp.ui.theme.VelibAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VelibViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            VelibAppTheme {
                VelibNavGraph(viewModel = viewModel)
            }
        }
    }
}