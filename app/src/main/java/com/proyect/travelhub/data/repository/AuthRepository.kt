package com.proyect.travelhub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proyect.travelhub.data.model.User
import com.proyect.travelhub.data.model.UserRole
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun registerUser(email: String, pass: String, name: String, role: UserRole, phone: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Error al obtener UID del usuario")

            val user = User(
                uid = uid,
                name = name,
                email = email,
                role = role,
                phone = phone
            )

            firestore.collection("users").document(uid).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

            val userDoc = firestore.collection("users").document(uid).get().await()
            var user = userDoc.toObject(User::class.java)

            // Si el perfil no existía en Firestore, crearlo por defecto
            if (user == null) {
                user = User(
                    uid = uid,
                    name = authResult.user?.displayName ?: email.substringBefore("@"),
                    email = email,
                    role = UserRole.TURISTA
                )
                firestore.collection("users").document(uid).set(user).await()
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserProfile(): User? {
        val uid = currentUserId ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(name: String, phone: String, avatarUrl: String): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("No autenticado"))
        return try {
            val updates = mapOf(
                "name" to name,
                "phone" to phone,
                "avatarUrl" to avatarUrl
            )
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<User> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Error al obtener usuario de Google")

            val uid = firebaseUser.uid
            val userDoc = firestore.collection("users").document(uid).get().await()
            var user = userDoc.toObject(User::class.java)

            if (user == null) {
                user = User(
                    uid = uid,
                    name = firebaseUser.displayName ?: "Usuario Google",
                    email = firebaseUser.email ?: "",
                    role = UserRole.TURISTA,
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                firestore.collection("users").document(uid).set(user).await()
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}