package com.proyect.travelhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyect.travelhub.data.model.UserRole
import com.proyect.travelhub.navigation.NavRoutes
import com.proyect.travelhub.ui.screens.auth.LoginScreen
import com.proyect.travelhub.ui.screens.auth.RegisterScreen
import com.proyect.travelhub.ui.screens.calculator.CostCalculatorScreen
import com.proyect.travelhub.ui.screens.catalog.CatalogScreen
import com.proyect.travelhub.ui.screens.chat.ChatScreen
import com.proyect.travelhub.ui.screens.itinerary.ItineraryScreen
import com.proyect.travelhub.ui.screens.profile.ProfileScreen
import com.proyect.travelhub.ui.screens.provider.ProviderDashboardScreen
import com.proyect.travelhub.ui.theme.TravelHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TravelHubAppNavigation()
                }
            }
        }
    }
}

@Composable
fun TravelHubAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    val destination = if (user.role == UserRole.PRESTADOR) {
                        NavRoutes.ProviderDashboard.route
                    } else {
                        NavRoutes.Catalog.route
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { user ->
                    val destination = if (user.role == UserRole.PRESTADOR) {
                        NavRoutes.ProviderDashboard.route
                    } else {
                        NavRoutes.Catalog.route
                    }
                    navController.navigate(destination) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.Catalog.route) {
            CatalogScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(NavRoutes.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToItinerary = {
                    navController.navigate(NavRoutes.Itinerary.route)
                },
                onNavigateToCalculator = {
                    navController.navigate(NavRoutes.CostCalculator.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onNavigateToChat = { chatId, otherUserId, name ->
                    navController.navigate(NavRoutes.Chat.createRoute(chatId, otherUserId, name))
                },
                onNavigateToChatList = {
                    navController.navigate(NavRoutes.ChatList.route)
                }
            )
        }

        composable(NavRoutes.Itinerary.route) {
            ItineraryScreen(
                onNavigateToCatalog = {
                    navController.navigate(NavRoutes.Catalog.route)
                },
                onNavigateToCalculator = {
                    navController.navigate(NavRoutes.CostCalculator.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onNavigateToChatList = {
                    navController.navigate(NavRoutes.ChatList.route)
                }
            )
        }

        composable(NavRoutes.CostCalculator.route) {
            CostCalculatorScreen(
                onNavigateToCatalog = {
                    navController.navigate(NavRoutes.Catalog.route)
                },
                onNavigateToItinerary = {
                    navController.navigate(NavRoutes.Itinerary.route)
                },
                onNavigateToChatList = {
                    navController.navigate(NavRoutes.ChatList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                }
            )
        }

        composable(NavRoutes.ChatList.route) {
            com.proyect.travelhub.ui.screens.chat.ChatListScreen(
                onConversationClick = { chatId, otherUserId, otherUserName ->
                    navController.navigate(NavRoutes.Chat.createRoute(chatId, otherUserId, otherUserName))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.Chat.route,
            arguments = listOf(
                androidx.navigation.navArgument("chatId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("otherUserId") { type = androidx.navigation.NavType.StringType; defaultValue = "" },
                androidx.navigation.navArgument("otherUserName") { type = androidx.navigation.NavType.StringType; defaultValue = "Prestador" }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: "general_chat"
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
            val otherUserName = backStackEntry.arguments?.getString("otherUserName") ?: "Prestador"
            ChatScreen(
                chatId = chatId,
                receiverId = otherUserId,
                otherUserName = otherUserName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderDashboard.route) {
            ProviderDashboardScreen(
                onNavigateToCatalog = {
                    navController.navigate(NavRoutes.Catalog.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onNavigateToChatList = {
                    navController.navigate(NavRoutes.ChatList.route)
                }
            )
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}