package com.proyect.travelhub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proyect.travelhub.data.model.Booking
import com.proyect.travelhub.data.model.BookingStatus
import kotlinx.coroutines.tasks.await

class BookingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val bookingsCollection = firestore.collection("bookings")

    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val docRef = bookingsCollection.document()
            val newBooking = booking.copy(id = docRef.id)
            docRef.set(newBooking).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTouristBookings(touristId: String): List<Booking> {
        return try {
            val snapshot = bookingsCollection.whereEqualTo("touristId", touristId).get().await()
            snapshot.toObjects(Booking::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}