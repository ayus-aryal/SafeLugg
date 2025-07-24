package com.example.safelugg.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Business: ${details.personalDetails.businessName}", style = MaterialTheme.typography.titleLarge)
        Text("Owner: ${details.personalDetails.ownerName}")
        Text("Phone: ${details.personalDetails.phoneNumber}")
        Text("Email: ${details.personalDetails.email}")

        Spacer(Modifier.height(16.dp))
        Divider()

        Text("📍 Location", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Text("${details.locationDetails.streetAddress}, ${details.locationDetails.city}, ${details.locationDetails.state}, ${details.locationDetails.country}")
        Text("Postal Code: ${details.locationDetails.postalCode}")
        Text("Landmark: ${details.locationDetails.landmark}")
        Text("Location Note: ${details.locationDetails.locationText}")

        Spacer(Modifier.height(16.dp))
        Divider()

        Text("📦 Storage Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Text("Capacity: ${details.storageDetails.capacity} bags")
        Text("Types: ${details.storageDetails.storageTypes}")
        Text("Sizes: ${details.storageDetails.luggageSizes.joinToString()}")
        Text("Open Days: ${details.storageDetails.openDays.joinToString()}")
        Text("Open: ${details.storageDetails.openingTime} - ${details.storageDetails.closingTime}")
        Text("24x7: ${if (details.storageDetails.is24x7) "Yes" else "No"}")
        Text("CCTV: ${details.storageDetails.hasCCTV}, Locks: ${details.storageDetails.hasLocks}, Staff: ${details.storageDetails.hasStaff}")
        Text("Security Notes: ${details.storageDetails.securityNotes}")

        Spacer(Modifier.height(16.dp))
        Divider()

        Text("💰 Pricing", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Text("Price Per Bag: ₹${details.pricingDetails.pricePerBag}")
        Text("Note: ${details.pricingDetails.note}")

        Spacer(Modifier.height(16.dp))
        Divider()

        Text("📷 Images", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        LazyRow {
            items(details.imageUrls.size) { index ->
                Image(
                    painter = rememberAsyncImagePainter(details.imageUrls[index]),
                    contentDescription = "Image $index",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(120.dp)
                )
            }
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
                businessName = "Gada Electronics",
                ownerName = "Jethalal Gada",
                phoneNumber = "1234567890",
                email = "jetha@gada.com"
            ),
            locationDetails = LocationDetailsDto(
                country = "India",
                state = "Gujarat",
                city = "Rajkot",
                postalCode = "360001",
                streetAddress = "Plot no. 22, Main Market",
                landmark = "Opp. Navrang Hotel",
                locationText = "Near Railway Station"
            ),
            storageDetails = StorageDetailsDto(
                capacity = 50,
                storageTypes = "Locker, Room",
                luggageSizes = setOf("Small", "Medium", "Large"),
                hasCCTV = true,
                hasStaff = true,
                hasLocks = true,
                securityNotes = "Fully monitored",
                openDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri"),
                openingTime = "09:00",
                closingTime = "21:00",
                is24x7 = false
            ),
            pricingDetails = PricingDetailsDto(
                pricePerBag = 50.0,
                note = "Price for first 12 hours"
            ),
            imageUrls = listOf(
                "https://via.placeholder.com/150",
                "https://via.placeholder.com/160"
            )
        )
    )
}
