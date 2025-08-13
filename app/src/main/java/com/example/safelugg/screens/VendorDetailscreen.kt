package com.example.safelugg.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.safelugg.myviewmodels.*
import kotlinx.coroutines.delay

@Composable
fun VendorDetailsScreen(
    vendorId: Long,
    viewModel: VendorViewModel = viewModel()
) {
    val context = LocalContext.current

    val vendorDetails by viewModel.vendorDetails
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Fetch only once
    LaunchedEffect(Unit) {
        viewModel.fetchVendorDetails(vendorId)
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Text("Error: $errorMessage", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
        }

        vendorDetails != null -> {
            VendorDetailsContent(vendorDetails!!)
        }

        else -> {
            Text("No Data Found", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun VendorDetailsContent(details: VendorFullDetailsResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState()),
    ) {
        // Hero Section with Images and Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            // Image Gallery
            if (details.imageUrls.isNotEmpty()) {
                ImageGallerySection(details.imageUrls)
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
                    .padding(16.dp)
                    .background(
                        Color(0xFF1976D2),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "4.2",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Business Name and Star Rating
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = details.personalDetails.businessName,
                style = MaterialTheme.typography.headlineMedium,
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
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.StarOutline,
                    contentDescription = "Empty Star",
                    tint = Color(0xFFFFB000),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "4.2 • 127 reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // Feature Icons Row (like Booking.com)
        LazyRow(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FeatureIcon(
                    icon = Icons.Filled.Security,
                    label = if (details.storageDetails.hasCCTV) "CCTV" else "Secure"
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.Lock,
                    label = "Lockers"
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.PersonPin,
                    label = if (details.storageDetails.hasStaff) "Staffed" else "Self Service"
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.AccessTime,
                    label = if (details.storageDetails.is24x7) "24/7" else "Timed"
                )
            }
            item {
                FeatureIcon(
                    icon = Icons.Filled.LocalParking,
                    label = "Parking"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Content Cards
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pricing Card (Prominent like Booking.com)
            PricingCard(details.pricingDetails)

            // Storage Details Card
            ModernInfoCard(
                title = "Storage Details",
                icon = Icons.Filled.Inventory
            ) {
                StorageDetailsModern(details.storageDetails)
            }

            // Location Card with Map Style
            ModernInfoCard(
                title = "Location",
                icon = Icons.Filled.LocationOn
            ) {
                LocationDetailsModern(details.locationDetails)
            }

            // Contact Information Card
            ModernInfoCard(
                title = "Contact Information",
                icon = Icons.Filled.Phone
            ) {
                ContactDetailsModern(details.personalDetails)
            }

            // CTA Button
            Button(
                onClick = { /* Book now action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Book Storage",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ImageGallerySection(imageUrls: List<String>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(imageUrls.size) { index ->
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrls[index]),
                        contentDescription = "Storage Image ${index + 1}",
                        modifier = Modifier
                            .width(if (index == 0) 240.dp else 200.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Show +X more on last image if more than 3 images
                    if (index == 2 && imageUrls.size > 3) {
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
                                text = "+${imageUrls.size - 3}",
                                color = Color.White,
                                fontSize = 24.sp,
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
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color(0xFFF0F0F0)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(12.dp),
                tint = Color(0xFF1976D2)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PricingCard(pricingDetails: PricingDetailsDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Price per bag",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "₹${pricingDetails.pricePerBag.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = " per bag",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                if (pricingDetails.note.isNotEmpty()) {
                    Text(
                        text = pricingDetails.note,
                        style = MaterialTheme.typography.bodySmall,
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ModernInfoCard(
    title: String,
    icon: ImageVector,
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            content()
        }
    }
}

@Composable
fun StorageDetailsModern(storageDetails: StorageDetailsDto) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Capacity and Storage Types
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoChip(
                label = "Capacity",
                value = "${storageDetails.capacity} bags",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            InfoChip(
                label = "Storage Type",
                value = storageDetails.storageTypes,
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Operating Hours",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = if (storageDetails.is24x7) "24/7 Available"
                    else "${storageDetails.openingTime} - ${storageDetails.closingTime}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Luggage Sizes
        Column {
            Text(
                text = "Accepted Luggage Sizes",
                style = MaterialTheme.typography.bodySmall,
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Security Features",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = storageDetails.securityNotes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun LocationDetailsModern(locationDetails: LocationDetailsDto) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Address
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Address",
                tint = Color(0xFFFF5722),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Address",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "${locationDetails.streetAddress}, ${locationDetails.city}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${locationDetails.state}, ${locationDetails.country} - ${locationDetails.postalCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // Landmark
        if (locationDetails.landmark.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Landmark",
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Landmark",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = locationDetails.landmark,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Distance (mock data since not in original)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsWalk,
                contentDescription = "Distance",
                tint = Color(0xFF607D8B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "1.2 km from city center",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ContactDetailsModern(personalDetails: PersonalDetailsDto) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Owner",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Owner",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = personalDetails.ownerName,
                    style = MaterialTheme.typography.bodyLarge,
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = personalDetails.phoneNumber,
                style = MaterialTheme.typography.bodyLarge,
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = personalDetails.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVendorDetails() {
    VendorDetailsContent(
        VendorFullDetailsResponse(
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
                locationText = "Near International Airport"
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
                "https://via.placeholder.com/400x240",
                "https://via.placeholder.com/400x240",
                "https://via.placeholder.com/400x240",
                "https://via.placeholder.com/400x240"
            )
        )
    )
}