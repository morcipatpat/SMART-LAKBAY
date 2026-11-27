package com.smartlakbay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.smartlakbay.app.ui.ChatScreen
import com.smartlakbay.app.ui.IntroScreen
import com.smartlakbay.app.ui.ItineraryScreen
import com.smartlakbay.app.ui.LoginScreen
import com.smartlakbay.app.ui.MainScreen
import com.smartlakbay.app.ui.MapScreen
import com.smartlakbay.app.ui.RouteScreen
import com.smartlakbay.app.ui.TripCustomizerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartLakbayApp()
        }
    }
}

@Composable
fun SmartLakbayApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "intro") {
        composable("intro") { IntroScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("customizer/{destination}/{budget}") { backStack ->
            val dest = backStack.arguments?.getString("destination") ?: "Taal Heritage Town"
            val bud = backStack.arguments?.getString("budget")?.toIntOrNull() ?: 3500
            TripCustomizerScreen(navController, dest, bud)
        }
        composable("itinerary/{destination}/{budget}/{spotKeys}") { backStack ->
            val dest = backStack.arguments?.getString("destination") ?: "Taal Heritage Town"
            val bud = backStack.arguments?.getString("budget")?.toIntOrNull() ?: 3500
            val keys = backStack.arguments?.getString("spotKeys") ?: ""
            ItineraryScreen(navController, dest, bud, keys)
        }
        composable("route/{spotKeys}/{tripLabel}") { backStack ->
            val keys = backStack.arguments?.getString("spotKeys") ?: ""
            val label = java.net.URLDecoder.decode(backStack.arguments?.getString("tripLabel") ?: "Trip Route", "UTF-8")
            RouteScreen(navController, keys, label)
        }
        // Default to Lipa / Dagatan / Marauoy area (Home destination)
        composable("map") { MapScreen(LatLng(13.9550, 121.1600), navController) }
    }
}
