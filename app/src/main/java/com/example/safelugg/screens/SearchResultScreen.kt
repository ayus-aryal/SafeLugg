package com.example.safelugg.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.VendorResponse



// Data classes for filters
data class FilterState(
    val sortBy: SortOption = SortOption.NONE,
    val priceRange: PriceRange = PriceRange(0f, 10000f),
    val amenities: Set<Amenity> = emptySet(),
    val starRating: Int = 0
)

enum class SortOption(val displayName: String) {
    NONE("None"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low")
}

enum class Amenity(val displayName: String) {
    CCTV("CCTV Monitoring"),
    GUARD("In-place Guard"),
    SECURE_LOCKING("Secure Locking")
}

data class PriceRange(val min: Float, val max: Float)

@Composable
fun SearchResultScreen(
    location: String,
    date: String,
    bags: String,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CustomerViewModel = viewModel(),
) {
    val searchResults by viewModel.searchResults
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // State for editable search parameters
    var currentLocation by remember { mutableStateOf(location) }
    var currentDate by remember { mutableStateOf(date) }
    var currentBags by remember { mutableStateOf(bags) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Filter state
    var filterState by remember { mutableStateOf(FilterState()) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showFiltersDialog by remember { mutableStateOf(false) }

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Apply filters to search results
    val filteredAndSortedResults by remember(searchResults, filterState) {
        derivedStateOf {
            applyFilters(searchResults, filterState)
        }
    }

    LaunchedEffect(currentLocation, currentDate, currentBags) {
        viewModel.searchVendors(currentLocation, currentDate, currentBags.toIntOrNull() ?: 1)
    }

    // Use WindowInsets to handle system UI properly
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Header Section with responsive sizing
        HeaderSection(
            location = currentLocation,
            date = currentDate,
            bags = currentBags,
            onEditClick = { showEditDialog = true },
            onBackClick = onBackClick,
            screenWidth = screenWidth
        )

        // Filter Section with responsive horizontal scroll
        FilterSection(
            screenWidth = screenWidth,
            filterState = filterState,
            onSortClick = { showSortDialog = true },
            onFiltersClick = { showFiltersDialog = true }
        )

        // Properties List with responsive content
        PropertiesList(
            location = currentLocation,
            isLoading = isLoading,
            errorMessage = errorMessage,
            searchResults = filteredAndSortedResults,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }

    // Edit Search Dialog
    if (showEditDialog) {
        EditSearchDialog(
            location = currentLocation,
            date = currentDate,
            bags = currentBags,
            onLocationChange = { currentLocation = it },
            onDateChange = { currentDate = it },
            onBagsChange = { currentBags = it },
            onSearch = { showEditDialog = false },
            onDismiss = { showEditDialog = false },
            screenWidth = screenWidth
        )
    }

    // Sort Dialog
    if (showSortDialog) {
        SortDialog(
            currentSort = filterState.sortBy,
            onSortSelected = { sortOption ->
                filterState = filterState.copy(sortBy = sortOption)
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }

    // Filters Dialog
    if (showFiltersDialog) {
        FiltersDialog(
            filterState = filterState,
            onFilterStateChange = { filterState = it },
            onDismiss = { showFiltersDialog = false }
        )
    }
}

@Composable
fun HeaderSection(
    location: String,
    date: String,
    bags: String,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    // Responsive padding based on screen width
    val horizontalPadding = if (screenWidth < 360.dp) 12.dp else 16.dp
    val titleFontSize = if (screenWidth < 360.dp) 18.sp else 20.sp
    val subtitleFontSize = if (screenWidth < 360.dp) 10.sp else 12.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onEditClick() } // Make the header clickable
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(if (screenWidth < 360.dp) 40.dp else 48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(if (screenWidth < 360.dp) 20.dp else 24.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = location,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    maxLines = 1
                )
                Text(
                    text = "$date, $bags Bags",
                    fontSize = subtitleFontSize,
                    color = Color.Gray,
                    fontFamily = customFontFamily,
                    maxLines = 1
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(if (screenWidth < 360.dp) 40.dp else 48.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(if (screenWidth < 360.dp) 18.dp else 20.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "INR",
                    fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2196F3),
                    fontFamily = customFontFamily
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(if (screenWidth < 360.dp) 16.dp else 18.dp)
                )
            }
        }
    }
}

@Composable
fun EditSearchDialog(
    location: String,
    date: String,
    bags: String,
    onLocationChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onBagsChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    var editLocation by remember { mutableStateOf(location) }
    var editDate by remember { mutableStateOf(date) }
    var editBags by remember { mutableStateOf(bags) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Search",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = customFontFamily
                )

                OutlinedTextField(
                    value = editLocation,
                    onValueChange = { editLocation = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editDate,
                    onValueChange = { editDate = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editBags,
                    onValueChange = { editBags = it },
                    label = { Text("Number of Bags") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onLocationChange(editLocation)
                            onDateChange(editDate)
                            onBagsChange(editBags)
                            onSearch()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Search", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    screenWidth: androidx.compose.ui.unit.Dp,
    filterState: FilterState,
    onSortClick: () -> Unit,
    onFiltersClick: () -> Unit
) {
    val horizontalPadding = if (screenWidth < 360.dp) 12.dp else 16.dp
    val chipSpacing = if (screenWidth < 360.dp) 8.dp else 12.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(chipSpacing)
    ) {
        FilterChip(
            text = if (filterState.sortBy == SortOption.NONE) "Sort By" else filterState.sortBy.displayName,
            hasDropdown = true,
            screenWidth = screenWidth,
            isSelected = filterState.sortBy != SortOption.NONE,
            onClick = onSortClick
        )

        val hasActiveFilters = filterState.amenities.isNotEmpty() ||
                filterState.starRating > 0 ||
                filterState.priceRange.min > 0f ||
                filterState.priceRange.max < 10000f

        FilterChip(
            text = if (hasActiveFilters) "Filters Applied" else "All Filters",
            hasDropdown = true,
            screenWidth = screenWidth,
            isSelected = hasActiveFilters,
            onClick = onFiltersClick
        )

        FilterChip("Rush Deal", hasDropdown = true, screenWidth = screenWidth)
        FilterChip("Collections", hasDropdown = true, screenWidth = screenWidth)
    }
}

@Composable
fun FilterChip(
    text: String,
    hasDropdown: Boolean = false,
    screenWidth: androidx.compose.ui.unit.Dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val chipHeight = if (screenWidth < 360.dp) 32.dp else 36.dp
    val fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp
    val horizontalPadding = if (screenWidth < 360.dp) 10.dp else 12.dp

    Surface(
        modifier = Modifier
            .height(chipHeight)
            .clickable { onClick?.invoke() },
        shape = RoundedCornerShape(chipHeight / 2),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF2196F3) else Color.LightGray),
        color = if (isSelected) Color(0xFF2196F3).copy(alpha = 0.1f) else Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                color = if (isSelected) Color(0xFF2196F3) else Color.Black,
                fontFamily = customFontFamily
            )
            if (hasDropdown) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(if (screenWidth < 360.dp) 14.dp else 16.dp),
                    tint = if (isSelected) Color(0xFF2196F3) else Color.Gray
                )
            }
        }
    }
}

@Composable
fun SortDialog(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Sort By",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortSelected(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSort == option,
                            onClick = { onSortSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF2196F3)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option.displayName,
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = customFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FiltersDialog(
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var tempFilterState by remember { mutableStateOf(filterState) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Filters",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Price Range
                Text(
                    text = "Price Range (₹${tempFilterState.priceRange.min.toInt()} - ₹${tempFilterState.priceRange.max.toInt()})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                RangeSlider(
                    value = tempFilterState.priceRange.min..tempFilterState.priceRange.max,
                    onValueChange = { range ->
                        tempFilterState = tempFilterState.copy(
                            priceRange = PriceRange(range.start, range.endInclusive)
                        )
                    },
                    valueRange = 0f..10000f,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Star Rating
                Text(
                    text = "Minimum Star Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (0..5).forEach { rating ->
                        FilterChip(
                            text = if (rating == 0) "Any" else "$rating★",
                            screenWidth = 360.dp,
                            isSelected = tempFilterState.starRating == rating,
                            onClick = {
                                tempFilterState = tempFilterState.copy(starRating = rating)
                            }
                        )
                    }
                }

                // Amenities
                Text(
                    text = "Amenities",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontFamily = customFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Amenity.values().forEach { amenity ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val newAmenities = if (tempFilterState.amenities.contains(amenity)) {
                                    tempFilterState.amenities - amenity
                                } else {
                                    tempFilterState.amenities + amenity
                                }
                                tempFilterState = tempFilterState.copy(amenities = newAmenities)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempFilterState.amenities.contains(amenity),
                            onCheckedChange = { checked ->
                                val newAmenities = if (checked) {
                                    tempFilterState.amenities + amenity
                                } else {
                                    tempFilterState.amenities - amenity
                                }
                                tempFilterState = tempFilterState.copy(amenities = newAmenities)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF2196F3)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = amenity.displayName,
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontFamily = customFontFamily
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            tempFilterState = FilterState()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear All")
                    }

                    Button(
                        onClick = {
                            onFilterStateChange(tempFilterState)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Apply", color = Color.White)
                    }
                }
            }
        }
    }
}

// Helper function to safely convert price to float
fun String.safeToFloat(): Float {
    return try {
        this.replace(",", "").replace("₹", "").trim().toFloat()
    } catch (e: NumberFormatException) {
        0f
    }
}

// Helper function to apply filters and sorting
fun applyFilters(vendors: List<VendorResponse>, filterState: FilterState): List<VendorResponse> {
    var filtered = vendors

    // Apply price range filter
    filtered = filtered.filter { vendor ->
        val price = when (vendor.pricePerBag) {
            is String -> vendor.pricePerBag.safeToFloat()
            is Number -> vendor.pricePerBag.toFloat()
            else -> 0f
        }
        price >= filterState.priceRange.min && price <= filterState.priceRange.max
    }

    // Apply star rating filter (assuming you have a rating field in VendorResponse)
    if (filterState.starRating > 0) {
        filtered = filtered.filter { vendor ->
            // You'll need to add a rating field to VendorResponse or calculate it
            // For now, using a placeholder rating of 4.2
            val rating = try {
                // If you have a rating field: vendor.rating
                4.2f // placeholder - replace with actual rating field
            } catch (e: Exception) {
                4.2f
            }
            rating >= filterState.starRating
        }
    }

    // Apply amenities filter (you'll need to add amenities field to VendorResponse)
    if (filterState.amenities.isNotEmpty()) {
        filtered = filtered.filter { vendor ->
            // Placeholder logic - you'll need to implement based on your data structure
            filterState.amenities.all { amenity ->
                when (amenity) {
                    Amenity.CCTV -> true // vendor.amenities?.contains("CCTV") ?: false
                    Amenity.GUARD -> true // vendor.amenities?.contains("Guard") ?: false
                    Amenity.SECURE_LOCKING -> true // vendor.amenities?.contains("Locking") ?: false
                }
            }
        }
    }

    // Apply sorting
    return when (filterState.sortBy) {
        SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { vendor ->
            when (vendor.pricePerBag) {
                is String -> vendor.pricePerBag.safeToFloat()
                is Number -> vendor.pricePerBag.toFloat()
                else -> Float.MAX_VALUE
            }
        }
        SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { vendor ->
            when (vendor.pricePerBag) {
                is String -> vendor.pricePerBag.safeToFloat()
                is Number -> vendor.pricePerBag.toFloat()
                else -> 0f
            }
        }
        SortOption.NONE -> filtered
    }
}

@Composable
fun PropertiesList(
    location: String,
    isLoading: Boolean,
    errorMessage: String?,
    searchResults: List<VendorResponse>,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    val horizontalPadding = if (screenWidth < 360.dp) 12.dp else 16.dp
    val titleFontSize = if (screenWidth < 360.dp) 16.sp else 18.sp

    Column {
        Text(
            text = "Showing Properties in $location",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 12.dp),
            fontFamily = customFontFamily,
            maxLines = 2
        )

        when {
            isLoading -> CircularProgressIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp)
            )
            errorMessage != null -> Text(
                "Error: $errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(horizontalPadding),
                fontFamily = customFontFamily
            )
            searchResults.isEmpty() -> Text(
                "No vendors found.",
                modifier = Modifier.padding(horizontalPadding),
                fontFamily = customFontFamily
            )
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    bottom = 16.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults) { vendor ->
                    VendorCard(
                        vendor = vendor,
                        customFontFamily = customFontFamily,
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

@Composable
fun VendorCard(
    vendor: VendorResponse,
    customFontFamily: FontFamily,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val images = vendor.imageUrls.ifEmpty {
        listOf("https://via.placeholder.com/300x200.png?text=No+Image")
    }
    val pagerState = rememberPagerState { images.size }

    // Responsive sizing
    val cardPadding = if (screenWidth < 360.dp) 12.dp else 16.dp
    val imageHeight = if (screenWidth < 360.dp) 160.dp else 200.dp
    val contentPadding = if (screenWidth < 360.dp) 12.dp else 16.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = cardPadding),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Vendor Images with Badge
            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Vendor Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    )
                }

                // ValueStays Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2196F3)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(if (screenWidth < 360.dp) 14.dp else 16.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Text(
                                text = "₹",
                                fontSize = if (screenWidth < 360.dp) 8.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SafeLugg Verified",
                            fontSize = if (screenWidth < 360.dp) 10.sp else 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontFamily = customFontFamily
                        )
                    }
                }
            }

            // Dots indicator for multiple images
            if (images.size > 1) {
                DotsIndicator(
                    totalDots = pagerState.pageCount,
                    selectedIndex = pagerState.currentPage,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 4.dp),
                    screenWidth = screenWidth
                )
            }

            // Vendor Details with responsive content
            VendorDetails(
                vendor = vendor,
                customFontFamily = customFontFamily,
                screenWidth = screenWidth,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
fun VendorDetails(
    vendor: VendorResponse,
    customFontFamily: FontFamily,
    screenWidth: androidx.compose.ui.unit.Dp,
    contentPadding: androidx.compose.ui.unit.Dp
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        // Rating and Sponsored - Responsive
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF2196F3)
            ) {
                Text(
                    text = "4.2",
                    fontSize = if (screenWidth < 360.dp) 10.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontFamily = customFontFamily
                )
            }
            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Very Good",
                        fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = customFontFamily,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (screenWidth >= 360.dp) {
                        Text(
                            text = " (1566 Ratings)",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = customFontFamily
                        )
                    }
                }
                if (screenWidth < 360.dp) {
                    Text(
                        text = "(1566 Ratings)",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontFamily = customFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vendor Name and Stars - Responsive
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(if (screenWidth < 360.dp) 16.dp else 20.dp),
                tint = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = vendor.businessName,
                fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                fontFamily = customFontFamily,
                maxLines = 2
            )

            // Star Rating - Responsive size
            val starSize = if (screenWidth < 360.dp) 12.dp else 14.dp
            repeat(3) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = Color(0xFFFFB400)
                )
            }
            repeat(2) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Location and Price - Responsive layout
        if (screenWidth < 360.dp) {
            // Stack vertically on very small screens
            Column {
                Text(
                    text = "${vendor.city} | 2.0 km from center",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp,
                    fontFamily = customFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                PriceSection(vendor, customFontFamily, screenWidth)
            }
        } else {
            // Side by side on larger screens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${vendor.city} | 2.0 km from center",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp,
                        fontFamily = customFontFamily
                    )
                }
                PriceSection(vendor, customFontFamily, screenWidth)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Amenities - Responsive
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF9C27B0)
            ) {
                Icon(
                    Icons.Default.Create,
                    contentDescription = null,
                    modifier = Modifier
                        .size(if (screenWidth < 360.dp) 20.dp else 24.dp)
                        .padding(4.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Secure storage, CCTV monitoring, Verified Partner",
                fontSize = if (screenWidth < 360.dp) 10.sp else 12.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                fontFamily = customFontFamily,
                lineHeight = if (screenWidth < 360.dp) 14.sp else 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PriceSection(
    vendor: VendorResponse,
    customFontFamily: FontFamily,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = "₹ 9999",
            fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp,
            color = Color.Gray,
            textDecoration = TextDecoration.LineThrough,
            fontFamily = customFontFamily
        )
        Text(
            text = "₹ ${vendor.pricePerBag}",
            fontSize = if (screenWidth < 360.dp) 18.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = customFontFamily
        )
        Text(
            text = "+ ₹ 50 taxes & fees",
            fontSize = if (screenWidth < 360.dp) 8.sp else 10.sp,
            color = Color.Gray,
            fontFamily = customFontFamily
        )
        Text(
            text = "Per Bag",
            fontSize = if (screenWidth < 360.dp) 8.sp else 10.sp,
            color = Color.Gray,
            fontFamily = customFontFamily
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val dotSize = if (screenWidth < 360.dp) 6.dp else 8.dp
    val selectedDotSize = if (screenWidth < 360.dp) 8.dp else 10.dp

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == selectedIndex) selectedDotSize else dotSize)
                    .padding(2.dp)
                    .background(
                        if (index == selectedIndex) Color(0xFF2196F3) else Color.LightGray,
                        CircleShape
                    )
            )
        }
    }
}

// Preview functions for the responsive screen
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SearchResultScreenPreview() {
    SearchResultScreen(
        location = "New York",
        date = "2024-03-15",
        bags = "2",
        onEditClick = { },
        onBackClick = { }
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 568)
@Composable
fun SearchResultScreenSmallPreview() {
    SearchResultScreen(
        location = "New York",
        date = "2024-03-15",
        bags = "2",
        onEditClick = { },
        onBackClick = { }
    )
}

@Preview(showBackground = true, widthDp = 411, heightDp = 731)
@Composable
fun SearchResultScreenLargePreview() {
    SearchResultScreen(
        location = "New York",
        date = "2024-03-15",
        bags = "2",
        onEditClick = { },
        onBackClick = { }
    )
}