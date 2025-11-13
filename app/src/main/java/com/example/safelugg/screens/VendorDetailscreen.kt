package com.example.safelugg.screens


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.safelugg.model.BookingCreateRequest
import com.example.safelugg.model.BookingResponse
import com.example.safelugg.myviewmodels.BookingApi
import com.example.safelugg.myviewmodels.LocationDetailsDto
import com.example.safelugg.myviewmodels.PersonalDetailsDto
import com.example.safelugg.myviewmodels.PricingDetailsDto
import com.example.safelugg.myviewmodels.StorageDetailsDto
import com.example.safelugg.myviewmodels.VendorFullDetailsResponse
import com.example.safelugg.myviewmodels.VendorViewModel
import com.example.safelugg.utils.PreferenceHelper
import com.example.safelugg.utils.booking.BookingUtils.validateBookingTime
import com.example.safelugg.utils.booking.VendorOperatingHours
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.net.toUri
import com.example.safelugg.BuildConfig
import com.example.safelugg.MainActivity
import com.example.safelugg.myviewmodels.PaymentRetrofitInstance
import com.example.safelugg.utils.RazorpayCheckoutHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
fun parseCustomTime(timeString: String): LocalTime? {
    if (timeString.isBlank()) return null

    return try {
        val cleanInput = timeString.trim().uppercase()

        // Handle 12-hour format (with AM/PM)
        if (cleanInput.contains("AM") || cleanInput.contains("PM")) {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            LocalTime.parse(cleanInput, formatter)
        }
        // Handle 24-hour format (HH:MM)
        else if (cleanInput.contains(":")) {
            val parts = cleanInput.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull()
                val minute = parts[1].toIntOrNull()

                if (hour != null && minute != null &&
                    hour in 0..23 && minute in 0..59) {
                    LocalTime.of(hour, minute)
                } else null
            } else null
        }
        // Handle hour-only input (like "14" or "2")
        else {
            val hour = cleanInput.toIntOrNull()
            if (hour != null && hour in 0..23) {
                LocalTime.of(hour, 0)
            } else null
        }
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VendorDetailsScreen(
    vendorId: Long,
    bags: String,
    viewModel: VendorViewModel = viewModel(),
    bookingApi: BookingApi,
    onBookNowClick: (Long) -> Unit = {},
    onBookingCreated: (BookingResponse) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val vendorDetails by viewModel.vendorDetails
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Fetch only once
    LaunchedEffect(Unit) {
        viewModel.fetchVendorDetails(vendorId)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1976D2),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading vendor details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontFamily = customFontFamily

                        )
                    }
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontFamily = customFontFamily

                        )
                    }
                }
            }

            vendorDetails != null -> {

                val vendorOperatingHours = VendorOperatingHours(
                    is24x7 = vendorDetails!!.storageDetails.is24x7,
                    openTime = if (!vendorDetails!!.storageDetails.is24x7) {
                        LocalTime.parse(
                            vendorDetails!!.storageDetails.openingTime,
                            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
                        )
                    } else null,
                    closeTime = if (!vendorDetails!!.storageDetails.is24x7) {
                        LocalTime.parse(
                            vendorDetails!!.storageDetails.closingTime,
                            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
                        )
                    } else null,
                    openDays = vendorDetails!!.storageDetails.openDays
                )

                VendorDetailsContent(
                    details = vendorDetails!!,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    onBookNowClick = { onBookNowClick(vendorId) },
                    vendorId = vendorId,
                    initialBags = "\\d+".toRegex().find(bags)?.value?.toIntOrNull() ?: 0,
                    bookingApi = bookingApi,
                    onBookingCreated = onBookingCreated,
                    vendorOperatingHours = vendorOperatingHours
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Data Found",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = customFontFamily

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VendorDetailsContent(
    details: VendorFullDetailsResponse,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    onBookNowClick: () -> Unit,
    vendorId: Long,
    initialBags: Int,
    bookingApi: BookingApi,
    onBookingCreated: (BookingResponse) -> Unit,
    vendorOperatingHours: VendorOperatingHours

    ) {
    val isTablet = screenWidth > 600.dp
    val heroHeight = if (isTablet) 320.dp else minOf(screenHeight * 0.35f, 280.dp)
    val horizontalPadding = if (isTablet) 24.dp else 16.dp
    val cardPadding = if (isTablet) 24.dp else 20.dp
    var showPaymentDialog by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var showReviewSheet by remember { mutableStateOf(false) }
    var bookingRequest: BookingCreateRequest? by remember { mutableStateOf(null) }
    var loading by remember { mutableStateOf(false) }
    var hoursText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }



    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Space for floating button
        ) {
            // Hero Section with Images and Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                // Image Gallery
                if (details.imageUrls.isNotEmpty()) {
                    ImageGallerySection(
                        imageUrls = details.imageUrls,
                        isTablet = isTablet
                    )
                }

                // Gradient Overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Rating Badge (top right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontalPadding)
                        .background(
                            Color(0xFF1976D2),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "4.2★",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontFamily = customFontFamily

                    )
                }
            }

            // Business Name and Star Rating
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(cardPadding)
            ) {
                Text(
                    text = details.personalDetails.businessName,
                    style = if (isTablet)
                        MaterialTheme.typography.headlineLarge
                    else
                        MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = customFontFamily

                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(4) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFB000),
                            modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.StarOutline,
                        contentDescription = "Empty Star",
                        tint = Color(0xFFFFB000),
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4.2 • 127 reviews",
                        style = if (isTablet)
                            MaterialTheme.typography.bodyLarge
                        else
                            MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontFamily = customFontFamily

                    )
                }
            }

            // Feature Icons Row
            LazyRow(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = cardPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(if (isTablet) 32.dp else 24.dp)
            ) {
                item {
                    FeatureIcon(
                        icon = Icons.Filled.Security,
                        label = if (details.storageDetails.hasCCTV) "CCTV" else "Secure",
                        isTablet = isTablet
                    )
                }
                item {
                    FeatureIcon(
                        icon = Icons.Filled.Lock,
                        label = "Lockers",
                        isTablet = isTablet
                    )
                }
                item {
                    FeatureIcon(
                        icon = Icons.Filled.PersonPin,
                        label = if (details.storageDetails.hasStaff) "Staffed" else "Self Service",
                        isTablet = isTablet
                    )
                }
                item {
                    FeatureIcon(
                        icon = Icons.Filled.AccessTime,
                        label = if (details.storageDetails.is24x7) "24/7" else "Timed",
                        isTablet = isTablet
                    )
                }
                item {
                    FeatureIcon(
                        icon = Icons.Filled.LocalParking,
                        label = "Parking",
                        isTablet = isTablet
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Cards
            Column(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pricing Card (Prominent)
                PricingCard(
                    pricingDetails = details.pricingDetails,
                    isTablet = isTablet,
                    cardPadding = cardPadding
                )

                // Storage Details Card
                ModernInfoCard(
                    title = "Storage Details",
                    icon = Icons.Filled.Inventory,
                    isTablet = isTablet,
                    cardPadding = cardPadding
                ) {
                    StorageDetailsModern(
                        storageDetails = details.storageDetails,
                        isTablet = isTablet
                    )
                }

                // Location Card with Map Style
                ModernInfoCard(
                    title = "Location",
                    icon = Icons.Filled.LocationOn,
                    isTablet = isTablet,
                    cardPadding = cardPadding
                ) {
                    LocationDetailsModern(
                        locationDetails = details.locationDetails,
                        apiKey = BuildConfig.MAPS_API_KEY,
                        isTablet = isTablet
                    )
                }

                // Contact Information Card
                ModernInfoCard(
                    title = "Contact Information",
                    icon = Icons.Filled.Phone,
                    isTablet = isTablet,
                    cardPadding = cardPadding
                ) {
                    ContactDetailsModern(
                        personalDetails = details.personalDetails,
                        isTablet = isTablet
                    )
                }

                // Additional space for floating button
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Floating Book Now Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.8f),
                            Color.White
                        )
                    )
                )
                .padding(horizontal = horizontalPadding, vertical = 16.dp)
        ) {
            var showDialog by remember { mutableStateOf(false) }
            var hoursText by remember { mutableStateOf("") }
            val ctx = LocalContext.current
            var loading by remember { mutableStateOf(false) } // <--- REQUIRED
            val scope = rememberCoroutineScope()
            var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
            var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
            var showPaymentDialog by remember { mutableStateOf(false) }





            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 64.dp else 56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookOnline,
                        contentDescription = "Book",
                        tint = Color.White,
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Book Storage Now",
                        fontSize = if (isTablet) 18.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontFamily = customFontFamily

                    )
                }
            }
            if (showDialog) {
                var showDatePicker by remember { mutableStateOf(false) }
                var showTimePicker by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { if (!loading) showDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Book Storage",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily

                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            if (showDatePicker) {
                                // Custom Date Picker
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Select Date",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = customFontFamily

                                            )
                                            IconButton(
                                                onClick = { showDatePicker = false }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Quick date options
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val today = LocalDate.now()
                                            listOf(
                                                "Today",
                                                "Tomorrow",
                                                "Day After"
                                            ).forEachIndexed { index, label ->
                                                val date = today.plusDays(index.toLong())
                                                val isSelected = selectedDate == date

                                                Surface(
                                                    onClick = {
                                                        selectedDate = date
                                                        showDatePicker = false
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (isSelected)
                                                            MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.outline
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(12.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = if (isSelected)
                                                                MaterialTheme.colorScheme.onPrimary
                                                            else MaterialTheme.colorScheme.onSurface,
                                                            fontWeight = FontWeight.Medium,
                                                            fontFamily = customFontFamily

                                                        )
                                                        Text(
                                                            text = date.format(
                                                                DateTimeFormatter.ofPattern(
                                                                    "MMM dd"
                                                                )
                                                            ),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = if (isSelected)
                                                                MaterialTheme.colorScheme.onPrimary.copy(
                                                                    alpha = 0.8f
                                                                )
                                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontFamily = customFontFamily

                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Calendar grid for current month
                                        val today = LocalDate.now()
                                        val firstDayOfMonth = today.withDayOfMonth(1)
                                        val daysInMonth = today.lengthOfMonth()
                                        val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

                                        Text(
                                            text = today.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 12.dp),
                                            fontFamily = customFontFamily

                                        )

                                        // Days of week header
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            listOf(
                                                "S",
                                                "M",
                                                "T",
                                                "W",
                                                "T",
                                                "F",
                                                "S"
                                            ).forEach { day ->
                                                Text(
                                                    text = day,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.weight(1f),
                                                    textAlign = TextAlign.Center,
                                                    fontFamily = customFontFamily

                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Calendar grid
                                        for (week in 0..5) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                for (dayIndex in 0..6) {
                                                    val dayNumber =
                                                        week * 7 + dayIndex - startDayOfWeek + 1

                                                    if (dayNumber in 1..daysInMonth) {
                                                        val date = today.withDayOfMonth(dayNumber)
                                                        val isSelected = selectedDate == date
                                                        val isToday = date == LocalDate.now()
                                                        val isPast = date.isBefore(LocalDate.now())

                                                        Surface(
                                                            onClick = {
                                                                if (!isPast) {
                                                                    selectedDate = date
                                                                    showDatePicker = false
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .aspectRatio(1f)
                                                                .padding(2.dp),
                                                            color = when {
                                                                isSelected -> MaterialTheme.colorScheme.primary
                                                                isToday -> MaterialTheme.colorScheme.primaryContainer.copy(
                                                                    alpha = 0.3f
                                                                )

                                                                else -> Color.Transparent
                                                            },
                                                            shape = CircleShape,
                                                            border = if (isToday && !isSelected)
                                                                BorderStroke(
                                                                    1.dp,
                                                                    MaterialTheme.colorScheme.primary
                                                                )
                                                            else null
                                                        ) {
                                                            Box(
                                                                contentAlignment = Alignment.Center,
                                                                modifier = Modifier.fillMaxSize()
                                                            ) {
                                                                Text(
                                                                    text = dayNumber.toString(),
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    color = when {
                                                                        isPast -> MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                                            alpha = 0.4f
                                                                        )

                                                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                                                        else -> MaterialTheme.colorScheme.onSurface
                                                                    },
                                                                    fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal,
                                                                    fontFamily = customFontFamily

                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (showTimePicker) {
                                // Custom Time Picker
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Select Time",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = customFontFamily

                                            )
                                            IconButton(
                                                onClick = { showTimePicker = false }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Quick time options
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(3),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val timeOptions = listOf(
                                                "09:00", "10:00", "11:00",
                                                "12:00", "13:00", "14:00",
                                                "15:00", "16:00", "17:00",
                                                "18:00", "19:00", "20:00"
                                            )

                                            items(timeOptions.size) { index ->
                                                val timeStr = timeOptions[index]
                                                val time = LocalTime.parse(timeStr)
                                                val isSelected = selectedTime == time

                                                Surface(
                                                    onClick = {
                                                        selectedTime = time
                                                        showTimePicker = false
                                                    },
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (isSelected)
                                                            MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.outline
                                                    )
                                                ) {
                                                    Text(
                                                        text = timeStr,
                                                        modifier = Modifier.padding(16.dp),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = if (isSelected)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.onSurface,
                                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = customFontFamily

                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Custom time input section
                                        Column {
                                            Text(
                                                text = "Or enter custom time:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = customFontFamily

                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            var customTimeText by remember { mutableStateOf("") }
                                            var timeInputError by remember {
                                                mutableStateOf<String?>(
                                                    null
                                                )
                                            }

                                            OutlinedTextField(
                                                value = customTimeText,
                                                onValueChange = { input ->
                                                    // Allow only digits, colon, and spaces, limit length
                                                    if (input.all { it.isDigit() || it == ':' || it == ' ' || it.isLetter() } && input.length <= 8) {
                                                        customTimeText = input
                                                        timeInputError = null
                                                    }
                                                },
                                                label = { Text("Time") },
                                                placeholder = { Text("HH:MM or HH:MM AM/PM") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                isError = timeInputError != null,
                                                supportingText = if (timeInputError != null) {
                                                    {
                                                        Text(
                                                            timeInputError!!,
                                                            color = MaterialTheme.colorScheme.error,
                                                            fontFamily = customFontFamily
                                                            )
                                                    }
                                                } else {
                                                    {
                                                        Text(
                                                            "Format: 14:30 or 2:30 PM",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontFamily = customFontFamily

                                                        )
                                                    }
                                                },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Text,
                                                    imeAction = ImeAction.Done
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        val parsedTime =
                                                            parseCustomTime(customTimeText.trim())
                                                        if (parsedTime != null) {
                                                            selectedTime = parsedTime
                                                            showTimePicker = false
                                                            customTimeText = ""
                                                            timeInputError = null
                                                        } else {
                                                            timeInputError = "Invalid time format"
                                                        }
                                                    }
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Button(
                                                onClick = {
                                                    val parsedTime =
                                                        parseCustomTime(customTimeText.trim())
                                                    if (parsedTime != null) {
                                                        selectedTime = parsedTime
                                                        showTimePicker = false
                                                        customTimeText = ""
                                                        timeInputError = null
                                                    } else {
                                                        timeInputError =
                                                            "Please enter a valid time (e.g., 14:30 or 2:30 PM)"
                                                    }
                                                },
                                                enabled = customTimeText.isNotBlank(),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Set Custom Time")
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Main booking form
                                // Date and Time Section
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "When do you need storage?",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = customFontFamily

                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Date Picker Button
                                            Surface(
                                                onClick = { showDatePicker = true },
                                                modifier = Modifier.weight(1f),
                                                color = if (selectedDate != null)
                                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.5f
                                                    )
                                                else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (selectedDate != null)
                                                        MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.outline
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CalendarMonth,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = if (selectedDate != null)
                                                            MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = selectedDate?.format(
                                                            DateTimeFormatter.ofPattern("MMM dd, yyyy")
                                                        ) ?: "Select Date",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (selectedDate != null) FontWeight.SemiBold else FontWeight.Normal,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = customFontFamily

                                                    )
                                                }
                                            }

                                            // Time Picker Button
                                            Surface(
                                                onClick = { showTimePicker = true },
                                                modifier = Modifier.weight(1f),
                                                color = if (selectedTime != null)
                                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.5f
                                                    )
                                                else MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (selectedTime != null)
                                                        MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.outline
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AccessTime,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp),
                                                        tint = if (selectedTime != null)
                                                            MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = selectedTime?.format(
                                                            DateTimeFormatter.ofPattern("HH:mm")
                                                        ) ?: "Select Time",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (selectedTime != null) FontWeight.SemiBold else FontWeight.Normal,
                                                        textAlign = TextAlign.Center,
                                                        fontFamily = customFontFamily

                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Duration Section
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Timer,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Duration (Hours)",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = customFontFamily

                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = hoursText,
                                            onValueChange = { input ->
                                                if (input.all { it.isDigit() } && input.length <= 3) hoursText =
                                                    input
                                            },
                                            label = {
                                                Text(
                                                    "Enter hours",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontFamily = customFontFamily

                                                )
                                            },
                                            placeholder = {
                                                Text(
                                                    "e.g., 2",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.6f
                                                    ),
                                                    fontFamily = customFontFamily

                                                )
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            trailingIcon = {
                                                if (hoursText.isNotEmpty()) {
                                                    Text(
                                                        text = if (hoursText.toIntOrNull() == 1) "hour" else "hours",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(end = 8.dp),
                                                        fontFamily = customFontFamily

                                                    )
                                                }
                                            }
                                        )

                                        // Duration suggestions
                                        if (hoursText.isEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                items(5) { index ->
                                                    val hours =
                                                        listOf("1", "2", "4", "8", "24")[index]
                                                    FilterChip(
                                                        text = "$hours ${if (hours == "1") "hr" else "hrs"}",
                                                        onClick = { hoursText = hours },
                                                        screenWidth = 120.dp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (loading) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.1f
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 3.dp
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "Creating your booking...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontFamily = customFontFamily

                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        if (!showDatePicker && !showTimePicker) {
                            Button(
                                enabled = !loading,
                                onClick = {
                                    val hours = hoursText.toIntOrNull()
                                    if (hours == null || hours <= 0) {
                                        Toast.makeText(
                                            ctx,
                                            "Enter a valid number of hours",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    if (selectedDate == null || selectedTime == null) {
                                        Toast.makeText(
                                            ctx,
                                            "Please select date and time",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    // Validate booking time against vendor operating hours
                                    val validationResult = validateBookingTime(
                                        selectedDate = selectedDate!!,
                                        selectedTime = selectedTime!!,
                                        durationHours = hours,
                                        vendorOperatingHours = vendorOperatingHours
                                    )

                                    if (!validationResult.isValid) {
                                        Toast.makeText(
                                            ctx,
                                            validationResult.errorMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@Button
                                    }

                                    val currentUserId = PreferenceHelper.getUserId(ctx)
                                    if (currentUserId <= 0L) {
                                        Toast.makeText(
                                            ctx,
                                            "Please log in first",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    // Combine date + time
                                    val start = LocalDateTime.of(selectedDate, selectedTime)
                                    val end = start.plusHours(hours.toLong())

                                    val request = BookingCreateRequest(
                                        vendorId = vendorId,
                                        bookingDate = start.toLocalDate()
                                            .format(DateTimeFormatter.ISO_LOCAL_DATE),
                                        scheduledStartTime = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                        scheduledEndTime = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                        noOfBags = initialBags,
                                        expectedHours = hours
                                    )

                                    // Save request & open review bottom sheet
                                    bookingRequest = request
                                    showDialog = false
                                    showReviewSheet = true
                                },
                                modifier = Modifier.padding(horizontal = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (!loading) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = if (loading) "Booking..." else "Confirm Booking",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = customFontFamily

                                )
                            }
                        }
                    },
                    dismissButton = {
                        if (!showDatePicker && !showTimePicker) {
                            TextButton(onClick = { if (!loading) showDialog = false }) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontFamily = customFontFamily
                                )
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp
                )
            }

            // Review Bottom Sheet
            if (showReviewSheet && bookingRequest != null) {
                ModalBottomSheet(
                    onDismissRequest = { showReviewSheet = false },
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = {
                        Surface(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 32.dp, height = 4.dp)
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        // Header with close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Review Your Booking",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = customFontFamily

                                )
                            )
                            IconButton(
                                onClick = { showReviewSheet = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Booking details card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                // Booking icon and title
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .padding(12.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Text(
                                        text = "Booking Summary",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = customFontFamily

                                        )
                                    )
                                }

                                Spacer(Modifier.height(20.dp))

                                // Booking details with icons
                                BookingDetailRow(
                                    icon = Icons.Default.BusinessCenter,
                                    label = "Number of Bags",
                                    value = "${bookingRequest!!.noOfBags}"
                                )

                                Spacer(Modifier.height(16.dp))

                                BookingDetailRow(
                                    icon = Icons.Default.AccessTime,
                                    label = "Expected Hours",
                                    value = "${bookingRequest!!.expectedHours}"
                                )

                                Spacer(Modifier.height(16.dp))

                                BookingDetailRow(
                                    icon = Icons.Default.PlayArrow,
                                    label = "Start Time",
                                    value = bookingRequest!!.scheduledStartTime
                                )

                                Spacer(Modifier.height(16.dp))

                                BookingDetailRow(
                                    icon = Icons.Default.Stop,
                                    label = "End Time",
                                    value = bookingRequest!!.scheduledEndTime
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        // Action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showReviewSheet = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = customFontFamily

                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    val currentUserId = PreferenceHelper.getUserId(ctx)
                                    if (currentUserId <= 0L) {
                                        Toast.makeText(
                                            ctx,
                                            "Please log in first",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        return@Button
                                    }

                                    scope.launch {
                                        loading = true
                                        try {
                                            // Create booking first
                                            val response = bookingApi.createBooking(
                                                currentUserId,
                                                bookingRequest!!
                                            )
                                            onBookingCreated(response)
                                            showReviewSheet = false
                                            showPaymentDialog = true

                                            // Initiate payment for that booking on server
                                            val paymentResp =
                                                PaymentRetrofitInstance.api.initiatePayment(response.bookingId)
                                            if (!paymentResp.isSuccessful) {
                                                loading = false
                                                Toast.makeText(
                                                    ctx,
                                                    "Payment initiation failed: ${paymentResp.code()}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                return@launch
                                            }
                                            val paymentInit = paymentResp.body() ?: run {
                                                loading = false
                                                Toast.makeText(
                                                    ctx,
                                                    "Empty response from payment init",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                return@launch
                                            }

                                            // Store paymentId in Activity so MainActivity can verify after checkout
                                            val activity = ctx as? Activity
                                            if (activity is MainActivity) {
                                                activity.lastInitiatedPaymentId =
                                                    paymentInit.paymentId
                                            }

                                            // Start Razorpay checkout
                                            val amountPaise = (paymentInit.amount * 100).toInt()
                                            val keyId = "rzp_test_RGASttiAoqXee1"
                                            //val keyId = BuildConfig.RAZORPAY_KEY_ID
                                            // apiKey = com.example.safelugg.BuildConfig.MAPS_API_KEY,


                                            val orderId = paymentInit.razorpayOrderId

                                            withContext(Dispatchers.Main) {
                                                val activity = ctx as? Activity
                                                if (activity != null) {
                                                    RazorpayCheckoutHelper.startCheckout(
                                                        activity,
                                                        keyId,
                                                        orderId,
                                                        amountPaise,
                                                        "SafeLugg"
                                                    )
                                                } else {
                                                    Toast.makeText(
                                                        ctx,
                                                        "Unable to start payment (context not Activity)",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }

                                            loading = false
                                        } catch (e: Exception) {
                                            loading = false
                                            e.printStackTrace()
                                            Toast.makeText(
                                                ctx,
                                                "Failed: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = !loading
                            ) {
                                if (loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payment,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Proceed & Pay",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Light,
                                                fontFamily = customFontFamily

                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }


            }


        }
    }



        }


@Composable
private fun BookingDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = customFontFamily
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = customFontFamily

                )
            )
        }
    }
}


@Composable
fun ImageGallerySection(
    imageUrls: List<String>,
    isTablet: Boolean
) {
    val firstImageWidth = if (isTablet) 300.dp else 240.dp
    val otherImageWidth = if (isTablet) 250.dp else 200.dp
    val horizontalPadding = if (isTablet) 24.dp else 16.dp

    Box(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ) {
            items(minOf(imageUrls.size, 4)) { index ->
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrls[index]),
                        contentDescription = "Storage Image ${index + 1}",
                        modifier = Modifier
                            .width(if (index == 0) firstImageWidth else otherImageWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Show +X more on last visible image if more than 3 images
                    if (index == 3 && imageUrls.size > 4) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${imageUrls.size - 4}",
                                color = Color.White,
                                fontSize = if (isTablet) 28.sp else 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureIcon(
    icon: ImageVector,
    label: String,
    isTablet: Boolean
) {
    val iconSize = if (isTablet) 56.dp else 48.dp
    val textSize = if (isTablet) 14.sp else 12.sp
    val columnWidth = if (isTablet) 80.dp else 64.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(columnWidth)
    ) {
        Surface(
            modifier = Modifier.size(iconSize),
            shape = CircleShape,
            color = Color(0xFFF0F0F0)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(if (isTablet) 16.dp else 12.dp),
                tint = Color(0xFF1976D2)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = textSize,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontFamily = customFontFamily

        )
    }
}

@Composable
fun PricingCard(
    pricingDetails: PricingDetailsDto,
    isTablet: Boolean,
    cardPadding: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Price per bag",
                    style = if (isTablet)
                        MaterialTheme.typography.bodyLarge
                    else
                        MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "₹${pricingDetails.pricePerBag.toInt()}",
                        style = if (isTablet)
                            MaterialTheme.typography.displaySmall
                        else
                            MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        fontFamily = customFontFamily

                    )
                    Text(
                        text = " per bag",
                        style = if (isTablet)
                            MaterialTheme.typography.bodyLarge
                        else
                            MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        fontFamily = customFontFamily

                    )
                }

                if (pricingDetails.note.isNotEmpty()) {
                    Text(
                        text = pricingDetails.note,
                        style = if (isTablet)
                            MaterialTheme.typography.bodyMedium
                        else
                            MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp),
                        fontFamily = customFontFamily

                    )
                }
            }

            // Discount Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF00C851)
            ) {
                Text(
                    text = "Best Price",
                    color = Color.White,
                    fontSize = if (isTablet) 14.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        horizontal = if (isTablet) 12.dp else 8.dp,
                        vertical = if (isTablet) 6.dp else 4.dp,

                    ),
                    fontFamily = customFontFamily


                )
            }
        }
    }
}

@Composable
fun ModernInfoCard(
    title: String,
    icon: ImageVector,
    isTablet: Boolean,
    cardPadding: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(cardPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = if (isTablet)
                        MaterialTheme.typography.headlineSmall
                    else
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = customFontFamily

                )
            }

            content()
        }
    }
}

@Composable
fun StorageDetailsModern(
    storageDetails: StorageDetailsDto,
    isTablet: Boolean
) {
    val textStyle = if (isTablet)
        MaterialTheme.typography.bodyLarge
    else
        MaterialTheme.typography.bodyMedium

    val iconSize = if (isTablet) 24.dp else 20.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Capacity and Storage Types
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoChip(
                label = "Capacity",
                value = "${storageDetails.capacity} bags",
                modifier = Modifier.weight(1f),
                isTablet = isTablet
            )
            Spacer(modifier = Modifier.width(12.dp))
            InfoChip(
                label = "Storage Type",
                value = storageDetails.storageTypes,
                modifier = Modifier.weight(1f),
                isTablet = isTablet
            )
        }

        // Operating Hours
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "Hours",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Operating Hours",
                    style = if (isTablet)
                        MaterialTheme.typography.bodyMedium
                    else
                        MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )
                Text(
                    text = if (storageDetails.is24x7) "24/7 Available"
                    else "${storageDetails.openingTime} - ${storageDetails.closingTime}",
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    fontFamily = customFontFamily

                )
            }
        }

        // Luggage Sizes
        Column {
            Text(
                text = "Accepted Luggage Sizes",
                style = if (isTablet)
                    MaterialTheme.typography.bodyMedium
                else
                    MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp),
                fontFamily = customFontFamily

            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(storageDetails.luggageSizes.size) { index ->
                    val size = storageDetails.luggageSizes.elementAt(index)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = size,
                            color = Color(0xFF1976D2),
                            fontSize = if (isTablet) 16.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(
                                horizontal = if (isTablet) 16.dp else 12.dp,
                                vertical = if (isTablet) 8.dp else 6.dp
                            ),
                            fontFamily = customFontFamily

                        )
                    }
                }
            }
        }

        // Security Features
        if (storageDetails.securityNotes.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Security",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Security Features",
                        style = if (isTablet)
                            MaterialTheme.typography.bodyMedium
                        else
                            MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = customFontFamily

                    )
                    Text(
                        text = storageDetails.securityNotes,
                        style = textStyle,
                        fontFamily = customFontFamily

                    )
                }
            }
        }
    }
}

@Composable
fun LocationDetailsModern(
    locationDetails: LocationDetailsDto,
    apiKey: String,
    isTablet: Boolean
) {
    val context = LocalContext.current
    val textStyle = if (isTablet)
        MaterialTheme.typography.bodyLarge
    else
        MaterialTheme.typography.bodyMedium
    val iconSize = if (isTablet) 24.dp else 20.dp
    val mapHeight = if (isTablet) 220.dp else 180.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Address
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Address",
                tint = Color(0xFFFF5722),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Address",
                    style = if (isTablet)
                        MaterialTheme.typography.bodyMedium
                    else
                        MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )
                Text(
                    text = "${locationDetails.streetAddress}, ${locationDetails.city}",
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    fontFamily = customFontFamily

                )
                Text(
                    text = "${locationDetails.state}, ${locationDetails.country} - ${locationDetails.postalCode}",
                    style = textStyle,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )
            }
        }

        // Landmark
        if (locationDetails.landmark.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Landmark",
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Landmark",
                        style = if (isTablet)
                            MaterialTheme.typography.bodyMedium
                        else
                            MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = customFontFamily

                    )
                    Text(
                        text = locationDetails.landmark,
                        style = textStyle,
                        fontFamily = customFontFamily

                    )
                }
            }
        }

        // Mini Map (Static Preview)
        val mapUrl = remember(locationDetails.latitude, locationDetails.longitude, apiKey) {
            "https://maps.googleapis.com/maps/api/staticmap?" +
                    "center=${locationDetails.latitude},${locationDetails.longitude}" +
                    "&zoom=15" +
                    "&size=600x300" +
                    "&markers=color:red%7C${locationDetails.latitude},${locationDetails.longitude}" +
                    "&key=$apiKey"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    val gmmIntentUri =
                        "geo:${locationDetails.latitude},${locationDetails.longitude}?q=${locationDetails.latitude},${locationDetails.longitude}(${locationDetails.streetAddress})".toUri()
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                }
        ) {
            SubcomposeAsyncImage(
                model = mapUrl,
                contentDescription = "Map Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    ShimmerEffect()
                }
            )
        }

        // Distance
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.DirectionsWalk,
                contentDescription = "Distance",
                tint = Color(0xFF607D8B),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "1.2 km from city center",
                style = textStyle,
                color = Color.Gray,
                fontFamily = customFontFamily

            )
        }
    }
}

@Composable
fun ContactDetailsModern(
    personalDetails: PersonalDetailsDto,
    isTablet: Boolean
) {
    val textStyle = if (isTablet)
        MaterialTheme.typography.bodyLarge
    else
        MaterialTheme.typography.bodyMedium
    val iconSize = if (isTablet) 24.dp else 20.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Owner",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Owner",
                    style = if (isTablet)
                        MaterialTheme.typography.bodyMedium
                    else
                        MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )
                Text(
                    text = personalDetails.ownerName,
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    fontFamily = customFontFamily

                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "Phone",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = personalDetails.phoneNumber,
                style = textStyle,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4CAF50),
                fontFamily = customFontFamily

            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email",
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = personalDetails.email,
                style = textStyle,
                color = Color(0xFFFF9800),
                fontFamily = customFontFamily

            )
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isTablet: Boolean
) {
    val chipPadding = if (isTablet) 20.dp else 16.dp

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(chipPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = if (isTablet)
                    MaterialTheme.typography.titleLarge
                else
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center,
                fontFamily = customFontFamily

            )
            Text(
                text = label,
                style = if (isTablet)
                    MaterialTheme.typography.bodyMedium
                else
                    MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = customFontFamily

            )
        }
    }
}

@Composable
fun ShimmerEffect() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerAnim"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        ),
        start = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim),
        end = androidx.compose.ui.geometry.Offset(translateAnim + 200f, translateAnim + 200f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    )
}

