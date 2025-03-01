package com.example.safelugg.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Correct usage of installSplashScreen()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Navigate to OnboardingActivity
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish() // Close splash screen
    }
}
