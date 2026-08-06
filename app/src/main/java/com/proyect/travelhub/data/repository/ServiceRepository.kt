package com.proyect.travelhub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem
import kotlinx.coroutines.tasks.await

class ServiceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val servicesCollection = firestore.collection("services")

    suspend fun getServices(category: ServiceCategory? = null): List<ServiceItem> {
        return try {
            val query = if (category != null) {
                servicesCollection.whereEqualTo("category", category.name)
            } else {
                servicesCollection
            }
            val snapshot = query.get().await()
            snapshot.toObjects(ServiceItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addService(service: ServiceItem): Result<String> {
        return try {
            val docRef = servicesCollection.document()
            val newService = service.copy(id = docRef.id)
            docRef.set(newService).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServicesByProvider(providerId: String): List<ServiceItem> {
        return try {
            val snapshot = servicesCollection.whereEqualTo("providerId", providerId).get().await()
            snapshot.toObjects(ServiceItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getServiceById(serviceId: String): ServiceItem? {
        return try {
            val snapshot = servicesCollection
                .document(serviceId)
                .get()
                .await()

            snapshot.toObject(ServiceItem::class.java)
        } catch (e: Exception) {
            null
        }
    }
}