package com.example.safelugg.screens

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.R
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            OnboardingScreen(navController)        }
    }
}


@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPage(
                    image = painterResource(id = getImageForPage(page)),
                    title = getTitleForPage(page),
                    description = getDescriptionForPage(page)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PageIndicator(totalPages = 3, currentPage = pagerState.currentPage)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < 2) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Navigate to Welcome Screen after onboarding
                            navController.navigate("welcome_screen"){
                                popUpTo("onboarding_screen") { inclusive = true}
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003366),
                    contentColor = Color.White
                )
            ) {
                Text(if (pagerState.currentPage < 2) "Next" else "Get Started")
            }
        }
    }
}


@Composable
fun PageIndicator(totalPages: Int, currentPage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until totalPages) {
            val isActive = i == currentPage
            Box(
                modifier = Modifier
                    .size(if (isActive) 15.dp else 15.dp)
                    .padding(4.dp)
                    .background(
                        color = if (isActive) Color(0xFF0072C6)
                        else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun OnboardingPage(image: Painter, title: String, description: String) {
    val interFontFamily = FontFamily(Font(R.font.inter))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = image, contentDescription = null, modifier = Modifier.size(300.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            fontSize = 30.sp,
            style = MaterialTheme.typography.bodyMedium,// Slightly larger for emphasis
            fontFamily = customFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF003366),
            letterSpacing = 1.5.sp, // Adds spacing between letters
            lineHeight = 34.sp, // Improves text readability
            textAlign = TextAlign.Center, // Centers the text
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = description,
            fontSize = 16.sp,
            fontFamily = customFontFamily,
            color = Color(0xFF003366),
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions
fun getImageForPage(page: Int): Int = when (page) {
    0 -> R.drawable.onboarding1
    1 -> R.drawable.onboarding2
    2 -> R.drawable.onboarding3
    else -> R.drawable.onboarding1
}

fun getTitleForPage(page: Int): String = when (page) {
    0 -> "Drop Your Bags"
    1 -> "Track It Anytime, Anywhere"
    2 -> "Explore Freely"
    else -> ""
}

fun getDescriptionForPage(page: Int): String = when (page) {
    0 -> "Securely store your luggage at trusted spots — no more dragging heavy bags around."
    1 -> "Get real-time updates and peace of mind while you focus on what matters"
    2 -> "Roam, relax, and enjoy—your bags are safe with us, always within the reach"
    else -> ""
}


