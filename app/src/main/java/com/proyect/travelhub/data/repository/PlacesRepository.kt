package com.proyect.travelhub.data.repository

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

data class PlaceSuggestion(
    val placeId: String,
    val description: String,
    val latLng: LatLng? = null
)

data class PlaceDetail(
    val name: String,
    val latLng: LatLng
)

class PlacesRepository(private val context: Context? = null) {

    private val apiKey = "AIzaSyDk6pnkrWcyb_3dCa6fC-csffA8Y6SnY4U"

    suspend fun searchPlaces(query: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
        if (query.length < 2) return@withContext emptyList()
        val results = mutableListOf<PlaceSuggestion>()

        // 1. Intentar primero con el Geocoder Nativo de Android (Funciona para Ilave, Juliaca, Puno, etc.)
        if (context != null && Geocoder.isPresent()) {
            try {
                val geocoder = Geocoder(context, Locale("es", "PE"))
                @Suppress("DEPRECATION")
                val searchQuery = if (!query.lowercase().contains("puno") && !query.lowercase().contains("peru")) {
                    "$query, Puno, Perú"
                } else query

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(searchQuery, 5)
                    ?: geocoder.getFromLocationName(query, 5)

                addresses?.forEach { address ->
                    val feature = address.featureName ?: ""
                    val line = if (address.maxAddressLineIndex >= 0) address.getAddressLine(0) else ""
                    val title = when {
                        line.isNotBlank() -> line
                        feature.isNotBlank() -> "$feature, Puno"
                        else -> query
                    }
                    results.add(
                        PlaceSuggestion(
                            placeId = "GEO_${address.latitude}_${address.longitude}",
                            description = title,
                            latLng = LatLng(address.latitude, address.longitude)
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("Places", "Error en Geocoder Nativo: ${e.message}")
            }
        }

        // 2. Fallback con Web API
        if (results.isEmpty()) {
            try {
                val encoded = URLEncoder.encode("$query Puno Peru", "UTF-8")
                val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encoded&key=$apiKey"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val status = json.optString("status")

                if (status == "OK") {
                    val jsonResults = json.getJSONArray("results")
                    for (i in 0 until jsonResults.length()) {
                        val item = jsonResults.getJSONObject(i)
                        val formatted = item.getString("formatted_address")
                        val loc = item.getJSONObject("geometry").getJSONObject("location")
                        val lat = loc.getDouble("lat")
                        val lng = loc.getDouble("lng")
                        results.add(
                            PlaceSuggestion(
                                placeId = "GEO_${lat}_${lng}",
                                description = formatted,
                                latLng = LatLng(lat, lng)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Places", "Error Web API: ${e.message}")
            }
        }

        return@withContext results
    }

    suspend fun getPlaceDetail(suggestion: PlaceSuggestion): PlaceDetail? = withContext(Dispatchers.IO) {
        if (suggestion.latLng != null) {
            return@withContext PlaceDetail(name = suggestion.description, latLng = suggestion.latLng)
        }
        return@withContext null
    }
}