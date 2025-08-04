package com.example.safelugg.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.R
import com.example.safelugg.myviewmodels.UserViewModel
import com.example.safelugg.utils.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth

class FillYourDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            FillYourDetailsScreen(navController)
        }
    }
}

// Import the existing AppColors and customFontFamily from your HomeScreen
// Make sure to add these imports at the top of your file:
// import com.example.safelugg.screens.AppColors
// import com.example.safelugg.screens.customFontFamily

// If you need additional colors not in the original AppColors, define them separately:
object AdditionalColors {
    val Error = Color(0xFFEF4444)
    val Success = Color(0xFF10B981)
}

@Composable
fun FillYourDetailsScreen(navController: NavController) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800)
    )

    // UI State
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }

    val userViewModel: UserViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val progress = calculateProgress(firstName, lastName, phoneNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .alpha(contentAlpha),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Section - matches HomeScreen style
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Complete Profile",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                fontFamily = customFontFamily
            )
            Text(
                text = "Help us personalize your SafeLugg experience",
                fontSize = 16.sp,
                color = AppColors.OnSurfaceVariant,
                fontFamily = customFontFamily
            )
        }

        // Progress Card - similar to search card aesthetic
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = AppColors.Primary.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profile Setup",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface,
                            fontFamily = customFontFamily
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Primary,
                        fontFamily = customFontFamily
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = AppColors.Primary,
                    trackColor = AppColors.SurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        // Form Card - matches HomeScreen search card style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = AppColors.Primary.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MinimalistFormField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = it.isBlank()
                    },
                    error = firstNameError,
                    errorMessage = "First name is required",
                    icon = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Text
                )

                MinimalistFormField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = it.isBlank()
                    },
                    error = lastNameError,
                    errorMessage = "Last name is required",
                    icon = Icons.Outlined.Person,
                    keyboardType = KeyboardType.Text
                )

                MinimalistFormField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneNumberError = it.isBlank()
                    },
                    error = phoneNumberError,
                    errorMessage = "Phone number is required",
                    icon = Icons.Outlined.Phone,
                    keyboardType = KeyboardType.Phone
                )
            }
        }

        // Action Button - matches HomeScreen search button
        Button(
            onClick = {
                firstNameError = firstName.isBlank()
                lastNameError = lastName.isBlank()
                phoneNumberError = phoneNumber.isBlank()

                if (!firstNameError && !lastNameError && !phoneNumberError) {
                    isLoading = true
                    errorMessage = null

                    userViewModel.createUser(
                        firstName = firstName,
                        lastName = lastName,
                        email = userEmail,
                        phoneNumber = phoneNumber,
                        onSuccess = {
                            PreferenceHelper.setUserLoggedIn(context, true)
                            Log.d("FillYourDetailsScreen", "User marked as logged in")

                            isLoading = false
                            Toast.makeText(context, "Profile created successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home_screen")
                        },
                        onError = { error ->
                            isLoading = false
                            val processedError = handleServerError(error)

                            // Show toast for duplicate phone number
                            if (processedError.contains("phone number is already registered", ignoreCase = true)) {
                                Toast.makeText(context, "📱 This phone number is already registered", Toast.LENGTH_LONG).show()
                            } else {
                                // For other errors, show dialog
                                errorMessage = processedError
                                showErrorDialog = true
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                disabledContainerColor = AppColors.OnSurfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Creating Profile...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = customFontFamily
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = customFontFamily
                    )
                }
            }
        }
    }

    // Error Dialog - clean and minimal
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                errorMessage = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = AdditionalColors.Error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = getErrorTitle(errorMessage ?: ""),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = customFontFamily,
                    color = AppColors.OnSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = errorMessage ?: "An unexpected error occurred",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontFamily = customFontFamily,
                        color = AppColors.OnSurfaceVariant
                    )

                    if (false) { // Remove phone-specific help text
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "💡 Try using a different phone number or contact support if this number belongs to you.",
                            fontSize = 14.sp,
                            color = AppColors.OnSurfaceVariant,
                            lineHeight = 20.sp,
                            fontFamily = customFontFamily
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        errorMessage = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppColors.Primary
                    )
                ) {
                    Text(
                        "Got it",
                        fontWeight = FontWeight.Medium,
                        fontFamily = customFontFamily
                    )
                }
            },
            dismissButton = null,
            shape = RoundedCornerShape(16.dp),
            containerColor = AppColors.Surface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalistFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean = false,
    errorMessage: String = "",
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label with icon - matches HomeScreen style
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (error) AdditionalColors.Error else AppColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface,
                fontFamily = customFontFamily
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    "Enter your $label",
                    color = AppColors.OnSurfaceVariant,
                    fontFamily = customFontFamily
                )
            },
            isError = error,
            trailingIcon = if (value.isNotEmpty() && !error) {
                {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AdditionalColors.Success,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.Outline,
                errorBorderColor = AdditionalColors.Error,
                focusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f),
                focusedTextColor = AppColors.OnSurface,
                unfocusedTextColor = AppColors.OnSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error && errorMessage.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = AdditionalColors.Error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = errorMessage,
                    color = AdditionalColors.Error,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = customFontFamily
                )
            }
        }
    }
}

private fun calculateProgress(firstName: String, lastName: String, phoneNumber: String): Float {
    var progress = 0f
    if (firstName.isNotBlank()) progress += 0.33f
    if (lastName.isNotBlank()) progress += 0.33f
    if (phoneNumber.isNotBlank()) progress += 0.34f
    return progress.coerceIn(0f, 1f)
}

private fun handleServerError(error: String): String {
    return when {
        error.contains("400", ignoreCase = true) ||
                error.contains("duplicate", ignoreCase = true) ||
                error.contains("already exists", ignoreCase = true) ||
                error.contains("phone", ignoreCase = true) -> {
            "This phone number is already registered with another account"
        }
        error.contains("401", ignoreCase = true) ||
                error.contains("unauthorized", ignoreCase = true) -> {
            "Authentication failed. Please sign in again"
        }
        error.contains("403", ignoreCase = true) ||
                error.contains("forbidden", ignoreCase = true) -> {
            "You don't have permission to perform this action"
        }
        error.contains("network", ignoreCase = true) ||
                error.contains("connection", ignoreCase = true) -> {
            "Network error. Please check your internet connection"
        }
        error.contains("timeout", ignoreCase = true) -> {
            "Request timed out. Please try again"
        }
        error.contains("500", ignoreCase = true) ||
                error.contains("server", ignoreCase = true) -> {
            "Server is temporarily unavailable. Please try again later"
        }
        else -> "Something went wrong. Please try again"
    }
}

private fun getErrorTitle(error: String): String {
    return when {
        error.contains("phone", ignoreCase = true) ||
                error.contains("duplicate", ignoreCase = true) -> "Phone Number Already Used"
        error.contains("network", ignoreCase = true) -> "Connection Problem"
        error.contains("server", ignoreCase = true) -> "Server Issue"
        error.contains("auth", ignoreCase = true) -> "Authentication Error"
        else -> "Oops!"
    }
}

@Preview(showBackground = true)
@Composable
fun FillYourDetailsScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        FillYourDetailsScreen(navController)
    }
}