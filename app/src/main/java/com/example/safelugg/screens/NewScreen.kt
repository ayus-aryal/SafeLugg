package com.example.safelugg.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.VendorResponse
import com.example.safelugg.screens.customFontFamily

@Composable
fun SearchResultScreen1(
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

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    LaunchedEffect(location, date, bags) {
        viewModel.searchVendors(location, date, bags.toIntOrNull() ?: 1)
    }

    // Use WindowInsets to handle system UI properly
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.systemBars) // Handles status bar and navigation bar
    ) {
        // Header Section with responsive sizing
        HeaderSection(
            location = location,
            date = date,
            bags = bags,
            onEditClick = onEditClick,
            onBackClick = onBackClick,
            screenWidth = screenWidth
        )

        // Filter Section with responsive horizontal scroll
        FilterSection(screenWidth = screenWidth)

        // Properties List with responsive content
        PropertiesList(
            location = location,
            isLoading = isLoading,
            errorMessage = errorMessage,
            searchResults = searchResults,
            screenWidth = screenWidth,
            screenHeight = screenHeight
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
            modifier = Modifier.weight(1f)
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
fun FilterSection(screenWidth: androidx.compose.ui.unit.Dp) {
    val horizontalPadding = if (screenWidth < 360.dp) 12.dp else 16.dp
    val chipSpacing = if (screenWidth < 360.dp) 8.dp else 12.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(chipSpacing)
    ) {
        FilterChip("Sort By", hasDropdown = true, screenWidth = screenWidth)
        FilterChip("All Filters", hasDropdown = true, screenWidth = screenWidth)
        FilterChip("Rush Deal", hasDropdown = true, screenWidth = screenWidth)
        FilterChip("Collections", hasDropdown = true, screenWidth = screenWidth)
    }
}

@Composable
fun FilterChip(
    text: String,
    hasDropdown: Boolean = false,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val chipHeight = if (screenWidth < 360.dp) 32.dp else 36.dp
    val fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp
    val horizontalPadding = if (screenWidth < 360.dp) 10.dp else 12.dp

    Surface(
        modifier = Modifier.height(chipHeight),
        shape = RoundedCornerShape(chipHeight / 2),
        border = BorderStroke(1.dp, Color.LightGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                color = Color.Black,
                fontFamily = customFontFamily
            )
            if (hasDropdown) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(if (screenWidth < 360.dp) 14.dp else 16.dp),
                    tint = Color.Gray
                )
            }
        }
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
                    bottom = 16.dp // Extra padding at bottom to avoid navigation bar
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

        // Login Section - Responsive

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

// Preview function for the responsive screen
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SearchResultScreenPreview() {
    SearchResultScreen1(
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
    SearchResultScreen1(
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
    SearchResultScreen1(
        location = "New York",
        date = "2024-03-15",
        bags = "2",
        onEditClick = { },
        onBackClick = { }
    )
}