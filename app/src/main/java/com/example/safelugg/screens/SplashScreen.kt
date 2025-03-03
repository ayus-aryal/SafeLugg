package com.example.safelugg.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.safelugg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("ContextCastToActivity")
@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        delay(2000) // ⏳ Show splash for 2 seconds

        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("firstName").orEmpty()
                        val lastName = document.getString("lastName").orEmpty()
                        val phoneNumber = document.getString("phoneNumber").orEmpty()

                        if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty()) {
                            // ✅ Profile complete → Navigate to Home
                            navController.navigate("home_screen") {
                                popUpTo("splash_screen") { inclusive = true }
                            }
                        } else {
                            // ⚠️ Profile incomplete → Fill Details
                            navController.navigate("fill_details") {
                                popUpTo("splash_screen") { inclusive = true }
                            }
                        }
                    } else {
                        // ⚠️ User not found in Firestore → Fill Details
                        navController.navigate("fill_your_details") {
                            popUpTo("splash_screen") { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener {
                    // 🔄 Firebase Error - Send to onboarding
                    navController.navigate("onboarding_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
        } else {
            // ❌ User not logged in → Go to Onboarding
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
            painter = painterResource(id = R.drawable.logo_splash_screen), // 🖼 Your app logo
            contentDescription = "App Logo",
            modifier = Modifier.size(500.dp)
        )
    }
}
