package fr.Yasmine.Nehad.velibapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location {
    val client = LocationServices.getFusedLocationProviderClient(context)

    // Essaie d'abord la dernière position connue (instantané)
    val lastLocation = suspendCancellableCoroutine<Location?> { cont ->
        client.lastLocation
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }

    if (lastLocation != null) return lastLocation

    // Sinon demande une position précise
    val cts = CancellationTokenSource()
    return suspendCancellableCoroutine { cont ->
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) cont.resume(location)
                else cont.resumeWithException(Exception("Localisation indisponible"))
            }
            .addOnFailureListener { cont.resumeWithException(it) }
        cont.invokeOnCancellation { cts.cancel() }
    }
}

fun distanceBetween(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}