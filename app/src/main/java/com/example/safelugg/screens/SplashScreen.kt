package com.example.safelugg.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.safelugg.R

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000) // ⏳ Show splash for 2 seconds
        navController.navigate("onboarding_screen") {
            popUpTo("splash_screen") { inclusive = true } // Remove splash from back stack
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_splash_screen), // 🖼 Replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(500.dp)
        )
    }
}
