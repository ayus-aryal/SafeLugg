package com.example.safelugg.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    val searchResults = viewModel.searchResults.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Results for: $location | $date | $bags bags")
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> Text("Loading...")
            errorMessage != null -> Text("Error: $errorMessage")
            searchResults.isEmpty() -> Text("No vendors found.")
            else -> LazyColumn {
                items(searchResults) { vendor ->
                    VendorCard(vendor)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun VendorCard(vendor: VendorResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Name: ${vendor.businessName}")
            Text("City: ${vendor.city}")
            Text("Price: ${vendor.pricePerBag} per bag")
            Text("Available Capacity: ${vendor.availableCapacity}")
        }
    }
}
