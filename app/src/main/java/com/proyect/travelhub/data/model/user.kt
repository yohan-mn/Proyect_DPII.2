package com.proyect.travelhub.data.model


enum class UserRole {
    TURISTA,
    PRESTADOR,
    ADMIN
}

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.TURISTA,
    val phone: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)