package com.example.safelugg.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safelugg.R

@Composable
fun WelcomeScreen(onGoogleSignInClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(1000)
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAFAFA),
                        Color(0xFFF5F5F5)
                    )
                )
            )
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // Logo with animation
            Image(
                painter = painterResource(id = R.drawable.logo_splash_screen),
                contentDescription = "SafeLugg Logo",
                modifier = Modifier
                    .size(400.dp)
                    .scale(logoScale)
                    .alpha(contentAlpha)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Welcome text
            Text(
                text = "Welcome to SafeLugg",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(contentAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your trusted travel companion for secure luggage management",
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .padding(horizontal = 16.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Google Sign-In Button
            ElevatedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(contentAlpha),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1A1A1A)
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms & Privacy Policy Text
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                fontSize = 13.sp,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .padding(horizontal = 8.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Subtle decorative elements
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Color(0x08000000),
                    RoundedCornerShape(60.dp)
                )
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Color(0x06000000),
                    RoundedCornerShape(40.dp)
                )
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            onGoogleSignInClick = { }
        )
    }
}