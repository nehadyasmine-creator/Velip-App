package fr.Yasmine.Nehad.velibapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openGoogleMaps(context: Context, lat: Double, lon: Double, name: String) {
    val uri = Uri.parse("google.navigation:q=$lat,$lon&mode=w")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lon&travelmode=walking")
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}