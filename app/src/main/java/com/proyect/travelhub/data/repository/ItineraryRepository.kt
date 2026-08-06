package com.proyect.travelhub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proyect.travelhub.data.model.ItineraryItem
import kotlinx.coroutines.tasks.await

class ItineraryRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val itinerariesCollection = firestore.collection("itineraries")

    suspend fun getTouristItinerary(touristId: String): List<ItineraryItem> {
        return try {
            val snapshot = itinerariesCollection
                .whereEqualTo("touristId", touristId)
                .get()
                .await()
            snapshot.toObjects(ItineraryItem::class.java).sortedBy { it.dayNumber }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addItineraryItem(item: ItineraryItem): Result<String> {
        return try {
            val docRef = itinerariesCollection.document()
            val newItem = item.copy(id = docRef.id)
            docRef.set(newItem).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItineraryItem(itemId: String): Result<Unit> {
        return try {
            itinerariesCollection.document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}