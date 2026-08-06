package com.proyect.travelhub.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Catalog : NavRoutes("catalog")
    object ServiceDetail : NavRoutes("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
    object Itinerary : NavRoutes("itinerary")
    object CostCalculator : NavRoutes("cost_calculator")
    object Chat : NavRoutes("chat/{chatId}/{otherUserId}/{otherUserName}") {
        fun createRoute(chatId: String, otherUserId: String = "", otherUserName: String = "Prestador") =
            "chat/$chatId/$otherUserId/$otherUserName"
    }
    object ChatList : NavRoutes("chat_list")
    object ProviderDashboard : NavRoutes("provider_dashboard")
    object Profile : NavRoutes("profile")
}