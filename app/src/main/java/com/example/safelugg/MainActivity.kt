package com.example.safelugg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.myviewmodels.GoogleSignInViewModel
import com.example.safelugg.screens.FillYourDetailsScreen
import com.example.safelugg.screens.MainScreen
import com.example.safelugg.screens.OnboardingScreen
import com.example.safelugg.screens.SplashScreen
import com.example.safelugg.screens.WelcomeScreen
import com.example.safelugg.ui.theme.SafeLuggTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeLugg()
        }
    }
}

@Composable
fun SafeLugg() {
    val navController = rememberNavController()
    val googleSignInViewModel = GoogleSignInViewModel()

    SafeLuggTheme {
        NavHost(navController = navController, startDestination = "splash_screen") {

            composable(route = "splash_screen") {
                SplashScreen(navController)
            }

            composable(route = "onboarding_screen") {
                OnboardingScreen(navController)
            }

            composable(route = "welcome_screen") {
                WelcomeScreen {
                    googleSignInViewModel.handleGoogleSignIn(navController.context, navController)
                }
            }

            composable(route = "fill_your_details") {
                FillYourDetailsScreen(navController)
            }

            composable(route = "home_screen") {
                MainScreen(navController)
            }
        }
    }
}
