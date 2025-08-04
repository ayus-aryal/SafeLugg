package com.example.safelugg.screens

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

// Modern Color Palette
object OnboardingColors {
    val Primary = Color(0xFF1A365D)
    val PrimaryLight = Color(0xFF2C5282)
    val Accent = Color(0xFF4299E1)
    val Background = Color(0xFFFAFAFA)
    val Surface = Color.White
    val TextPrimary = Color(0xFF1A202C)
    val TextSecondary = Color(0xFF718096)
    val Indicator = Color(0xFFE2E8F0)
}

class OnboardingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            OnboardingScreen(navController)
        }
    }
}

@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OnboardingColors.Background,
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < 2) {
                    TextButton(
                        onClick = {
                            navController.navigate("welcome_screen") {
                                popUpTo("onboarding_screen") { inclusive = true }
                            }
                        }
                    ) {
                        Text(
                            "Skip",
                            color = OnboardingColors.TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Content pager
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

            // Page indicator
            PageIndicator(
                totalPages = 3,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Navigation buttons
            NavigationButtons(
                currentPage = pagerState.currentPage,
                onBackClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNextClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < 2) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            navController.navigate("welcome_screen") {
                                popUpTo("onboarding_screen") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun OnboardingPage(
    image: Painter,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image with subtle shadow effect
        Card(
            modifier = Modifier.size(280.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title with modern typography
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = OnboardingColors.TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description with better readability
        Text(
            text = description,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = OnboardingColors.TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            Box(
                modifier = Modifier
                    .size(
                        width = if (isActive) 24.dp else 8.dp,
                        height = 8.dp
                    )
                    .clip(CircleShape)
                    .background(
                        color = if (isActive)
                            OnboardingColors.Accent
                        else
                            OnboardingColors.Indicator
                    )
            )
            if (index < totalPages - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun NavigationButtons(
    currentPage: Int,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        if (currentPage > 0) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.size(width = 100.dp, height = 48.dp),
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(OnboardingColors.Indicator, OnboardingColors.Indicator)
                    )
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = OnboardingColors.TextSecondary
                )
            ) {
                Text(
                    "Back",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Spacer(modifier = Modifier.size(width = 100.dp, height = 48.dp))
        }

        // Next/Get Started button
        Button(
            onClick = onNextClick,
            modifier = Modifier.size(
                width = if (currentPage < 2) 100.dp else 140.dp,
                height = 48.dp
            ),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnboardingColors.Primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = if (currentPage < 2) "Next" else "Get Started",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// Helper functions remain the same
fun getImageForPage(page: Int): Int = when (page) {
    0 -> R.drawable.onboarding1
    1 -> R.drawable.onboarding2
    2 -> R.drawable.onboarding3
    else -> R.drawable.onboarding1
}

fun getTitleForPage(page: Int): String = when (page) {
    0 -> "Drop Your Bags"
    1 -> "Track Anytime"
    2 -> "Explore Freely"
    else -> ""
}

fun getDescriptionForPage(page: Int): String = when (page) {
    0 -> "Securely store your luggage at trusted locations and explore without the weight."
    1 -> "Real-time updates and notifications keep you connected to your belongings."
    2 -> "Enjoy complete freedom while we keep your bags safe and accessible."
    else -> ""
}