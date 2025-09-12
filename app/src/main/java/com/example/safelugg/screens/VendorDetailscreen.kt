package com.example.safelugg.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.safelugg.BuildConfig
import com.example.safelugg.myviewmodels.LocationDetailsDto
import com.example.safelugg.myviewmodels.PersonalDetailsDto
import com.example.safelugg.myviewmodels.PricingDetailsDto
import com.example.safelugg.myviewmodels.StorageDetailsDto
import com.example.safelugg.myviewmodels.VendorFullDetailsResponse
import com.example.safelugg.myviewmodels.VendorViewModel

@Composable
fun VendorDetailsScreen(
    vendorId: Long,
    viewModel: VendorViewModel = viewModel(),
    onBookNowClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current
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
                            color = Color.Gray
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
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            vendorDetails != null -> {
                VendorDetailsContent(
                    details = vendorDetails!!,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    onBookNowClick = { onBookNowClick(vendorId) }
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
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun VendorDetailsContent(
    details: VendorFullDetailsResponse,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    onBookNowClick: () -> Unit
) {
    val isTablet = screenWidth > 600.dp
    val heroHeight = if (isTablet) 320.dp else minOf(screenHeight * 0.35f, 280.dp)
    val horizontalPadding = if (isTablet) 24.dp else 16.dp
    val cardPadding = if (isTablet) 24.dp else 20.dp

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
                        fontSize = if (isTablet) 18.sp else 16.sp
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
                    overflow = TextOverflow.Ellipsis
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
                        color = Color.Gray
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
                        color = Color.White
                    )
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Book Storage") },
                    text = {
                        OutlinedTextField(
                            value = hoursText,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) hoursText = input
                            },
                            label = { Text("Enter hours") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val hours = hoursText.toIntOrNull()
                                if (hours == null || hours <= 0) {
                                    Toast.makeText(ctx, "Enter valid hours", Toast.LENGTH_SHORT)
                                        .show()
                                    return@TextButton
                                }
                                showDialog = false
                                // ✅ Later we’ll call the API here
                                onBookNowClick()
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
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
            textAlign = TextAlign.Center
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
                    color = Color.Gray
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
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = " per bag",
                        style = if (isTablet)
                            MaterialTheme.typography.bodyLarge
                        else
                            MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
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
                        modifier = Modifier.padding(top = 4.dp)
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
                        vertical = if (isTablet) 6.dp else 4.dp
                    )
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
                    fontWeight = FontWeight.SemiBold
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
                    color = Color.Gray
                )
                Text(
                    text = if (storageDetails.is24x7) "24/7 Available"
                    else "${storageDetails.openingTime} - ${storageDetails.closingTime}",
                    style = textStyle,
                    fontWeight = FontWeight.Medium
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
                modifier = Modifier.padding(bottom = 8.dp)
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
                            )
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
                        color = Color.Gray
                    )
                    Text(
                        text = storageDetails.securityNotes,
                        style = textStyle
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
                    color = Color.Gray
                )
                Text(
                    text = "${locationDetails.streetAddress}, ${locationDetails.city}",
                    style = textStyle,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${locationDetails.state}, ${locationDetails.country} - ${locationDetails.postalCode}",
                    style = textStyle,
                    color = Color.Gray
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
                        color = Color.Gray
                    )
                    Text(
                        text = locationDetails.landmark,
                        style = textStyle
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
                    val gmmIntentUri = Uri.parse(
                        "geo:${locationDetails.latitude},${locationDetails.longitude}?q=${locationDetails.latitude},${locationDetails.longitude}(${locationDetails.streetAddress})"
                    )
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
                color = Color.Gray
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
                    color = Color.Gray
                )
                Text(
                    text = personalDetails.ownerName,
                    style = textStyle,
                    fontWeight = FontWeight.Medium
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
                color = Color(0xFF4CAF50)
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
                color = Color(0xFFFF9800)
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
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = if (isTablet)
                    MaterialTheme.typography.bodyMedium
                else
                    MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
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


@Preview(showBackground = true)
@Composable
fun PreviewVendorDetailsScreenPhone() {
    MaterialTheme {
        VendorDetailsContent(
            details = getSampleVendorDetails(),
            screenWidth = 360.dp,
            screenHeight = 640.dp,
            onBookNowClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun PreviewVendorDetailsScreenTablet() {
    MaterialTheme {
        VendorDetailsContent(
            details = getSampleVendorDetails(),
            screenWidth = 800.dp,
            screenHeight = 1280.dp,
            onBookNowClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewPricingCard() {
    MaterialTheme {
        PricingCard(
            pricingDetails = PricingDetailsDto(
                pricePerBag = 75.0,
                note = "First 24 hours. ₹25 for each additional 12 hours."
            ),
            isTablet = false,
            cardPadding = 20.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeatureIcons() {
    MaterialTheme {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                FeatureIcon(
                    icon = Icons.Filled.Security,
                    label = "CCTV",
                    isTablet = false
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.Lock,
                    label = "Lockers",
                    isTablet = false
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.PersonPin,
                    label = "Staffed",
                    isTablet = false
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.AccessTime,
                    label = "24/7",
                    isTablet = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStorageDetailsCard() {
    MaterialTheme {
        ModernInfoCard(
            title = "Storage Details",
            icon = Icons.Filled.Inventory,
            isTablet = false,
            cardPadding = 20.dp
        ) {
            StorageDetailsModern(
                storageDetails = StorageDetailsDto(
                    capacity = 75,
                    storageTypes = "Secure Lockers, Climate Controlled",
                    luggageSizes = setOf("Small", "Medium", "Large", "XL"),
                    hasCCTV = true,
                    hasStaff = true,
                    hasLocks = true,
                    securityNotes = "24/7 CCTV monitoring, biometric locks, on-site security staff",
                    openDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                    openingTime = "06:00",
                    closingTime = "23:00",
                    is24x7 = false
                ),
                isTablet = false
            )
        }
    }
}

// Sample data function for previews
private fun getSampleVendorDetails(): VendorFullDetailsResponse {
    return VendorFullDetailsResponse(
        vendorID = 1,
        personalDetails = PersonalDetailsDto(
            businessName = "SafeKeep Storage Hub Near Airport",
            ownerName = "Jethalal Gada",
            phoneNumber = "+91 98765 43210",
            email = "contact@safekeep.com"
        ),
        locationDetails = LocationDetailsDto(
            country = "India",
            state = "Gujarat",
            city = "Rajkot",
            postalCode = "360001",
            streetAddress = "Plot no. 22, Airport Road",
            landmark = "Opposite Metro Station",
            locationText = "Near International Airport",
            latitude = 22.3039,
            longitude = 70.8022
        ),
        storageDetails = StorageDetailsDto(
            capacity = 75,
            storageTypes = "Secure Lockers, Climate Controlled",
            luggageSizes = setOf("Small", "Medium", "Large", "XL"),
            hasCCTV = true,
            hasStaff = true,
            hasLocks = true,
            securityNotes = "24/7 CCTV monitoring, biometric locks, on-site security staff",
            openDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
            openingTime = "06:00",
            closingTime = "23:00",
            is24x7 = false
        ),
        pricingDetails = PricingDetailsDto(
            pricePerBag = 75.0,
            note = "First 24 hours. ₹25 for each additional 12 hours."
        ),
        imageUrls = listOf(
            "https://via.placeholder.com/400x240/1976D2/FFFFFF?text=Storage+1",
            "https://via.placeholder.com/400x240/4CAF50/FFFFFF?text=Storage+2",
            "https://via.placeholder.com/400x240/FF9800/FFFFFF?text=Storage+3",
            "https://via.placeholder.com/400x240/9C27B0/FFFFFF?text=Storage+4",
            "https://via.placeholder.com/400x240/F44336/FFFFFF?text=Storage+5"
        )
    )
}