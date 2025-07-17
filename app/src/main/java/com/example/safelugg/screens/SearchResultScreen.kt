package com.example.safelugg.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.VendorResponse

@Composable
fun SearchResultScreen(
    location: String,
    date: String,
    bags: String,
    viewModel: CustomerViewModel = viewModel()
) {
    LaunchedEffect(location, date, bags) {
        viewModel.searchVendors(location, date, bags.toIntOrNull() ?: 1)
    }

    val searchResults by viewModel.searchResults
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val insets = WindowInsets.systemBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = insets.calculateTopPadding() + 8.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Text(
            text = "$location | $date | $bags bags",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            searchResults.isEmpty() -> {
                Text(
                    text = "No vendors found.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = insets.calculateBottomPadding() + 16.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { vendor ->
                        VendorResultCard(vendor)
                    }
                }
            }
        }
    }
}




@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VendorResultCard(vendor: VendorResponse) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            BoxWithConstraints {
                val imageHeight = if (maxWidth < 360.dp) 140.dp else 180.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color(0xFF0072C6), shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = "4.2 ★ Very Good",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = vendor.businessName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${vendor.city} • 2.0 km from center",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                Text(
                    text = "10% OFF on your booking",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹ ${vendor.pricePerBag}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF0072C6),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹ 9999",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Verified Partner with secure storage & CCTV ✅",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
