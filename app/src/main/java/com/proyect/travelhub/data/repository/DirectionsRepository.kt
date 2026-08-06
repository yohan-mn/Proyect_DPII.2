package com.proyect.travelhub.data.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.math.*

data class RouteResult(
    val polylinePoints: List<LatLng> = emptyList(),
    val totalDurationText: String = "",
    val totalDistanceText: String = ""
)

class DirectionsRepository {

    private val apiKey = "AIzaSyDk6pnkrWcyb_3dCa6fC-csffA8Y6SnY4U"

    suspend fun getRoute(waypoints: List<LatLng>): RouteResult = withContext(Dispatchers.IO) {
        if (waypoints.size < 2) return@withContext RouteResult()

        try {
            val origin = "${waypoints.first().latitude},${waypoints.first().longitude}"
            val destination = "${waypoints.last().latitude},${waypoints.last().longitude}"

            val waypointsParam = if (waypoints.size > 2) {
                val middle = waypoints.subList(1, waypoints.size - 1)
                "&waypoints=" + middle.joinToString("|") { "${it.latitude},${it.longitude}" }
            } else ""

            val urlStr = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$origin" +
                    "&destination=$destination" +
                    waypointsParam +
                    "&mode=driving" +
                    "&language=es" +
                    "&key=$apiKey"

            val response = URL(urlStr).readText()
            val json = JSONObject(response)
            val status = json.optString("status")

            if (status == "OK") {
                val route = json.getJSONArray("routes").getJSONObject(0)
                val legs = route.getJSONArray("legs")

                var totalSeconds = 0L
                var totalMeters = 0L
                for (i in 0 until legs.length()) {
                    val leg = legs.getJSONObject(i)
                    totalSeconds += leg.getJSONObject("duration").getLong("value")
                    totalMeters += leg.getJSONObject("distance").getLong("value")
                }

                val durationText = formatDuration(totalSeconds)
                val distanceText = "%.1f km".format(totalMeters / 1000.0)

                val encodedPolyline = route.getJSONObject("overview_polyline").getString("points")
                val decodedPoints = decodePolyline(encodedPolyline)

                return@withContext RouteResult(decodedPoints, durationText, distanceText)
            }
        } catch (e: Exception) {
            Log.e("Directions", "Error conexión API: ${e.message}")
        }

        // Respaldo garantizado si hay agua/islas o fallos de red
        return@withContext calculateFallbackRoute(waypoints)
    }

    private fun calculateFallbackRoute(waypoints: List<LatLng>): RouteResult {
        var totalDistanceKm = 0.0
        for (i in 0 until waypoints.size - 1) {
            val dist = haversineDistance(
                waypoints[i].latitude, waypoints[i].longitude,
                waypoints[i + 1].latitude, waypoints[i + 1].longitude
            )
            totalDistanceKm += dist
        }

        val roadDistanceKm = (totalDistanceKm * 1.25 * 10).roundToInt() / 10.0
        val totalMinutes = (roadDistanceKm / 50.0 * 60).roundToInt()
        val durationText = formatDuration((totalMinutes * 60).toLong())
        val distanceText = "$roadDistanceKm km"

        return RouteResult(
            polylinePoints = waypoints,
            totalDurationText = durationText,
            totalDistanceText = distanceText
        )
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dLat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dLng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }
}