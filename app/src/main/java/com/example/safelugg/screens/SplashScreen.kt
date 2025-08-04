package com.example.safelugg.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.safelugg.R
import com.example.safelugg.utils.PreferenceHelper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000) // 2-second splash delay

        val isLoggedIn = PreferenceHelper.isUserLoggedIn(context)
        Log.d("SplashScreen", "Checking login status: $isLoggedIn")

        if (isLoggedIn) {
            Log.d("SplashScreen", "User is logged in, navigating to home_screen")
            navController.navigate("home_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        } else {
            Log.d("SplashScreen", "User not logged in, navigating to onboarding_screen")
            navController.navigate("onboarding_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_splash_screen),
            contentDescription = "App Logo",
            modifier = Modifier.size(500.dp)
        )
    }
}
