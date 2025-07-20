import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.VendorResponse
import com.example.safelugg.screens.customFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    location: String,
    date: String,
    bags: String,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CustomerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val searchResults by viewModel.searchResults
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(location, date, bags) {
        viewModel.searchVendors(location, date, bags.toIntOrNull() ?: 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Results", fontFamily = customFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SearchParamsHeader(location, date, bags, onEditClick, customFontFamily)
            Spacer(Modifier.height(8.dp))
            FilterChipsRow(customFontFamily)
            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                errorMessage != null -> Text("Error: $errorMessage", color = Color.Red)
                searchResults.isEmpty() -> Text("No vendors found.", fontFamily = customFontFamily)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(searchResults) { vendor ->
                        VendorResultCard(vendor, customFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchParamsHeader(
    location: String,
    date: String,
    bags: String,
    onEditClick: () -> Unit,
    customFontFamily: FontFamily
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("$location | $date | $bags bags", fontFamily = customFontFamily, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Search")
            }
        }
    }
}

@Composable
fun FilterChipsRow(customFontFamily: FontFamily) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            FilterChip("Nearby", customFontFamily)
            Spacer(Modifier.width(8.dp))
            FilterChip("Low Price", customFontFamily)
            Spacer(Modifier.width(8.dp))
            FilterChip("Verified", customFontFamily)
        }
    }
}

@Composable
fun FilterChip(text: String, customFontFamily: FontFamily) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color.Black),
        color = Color.White,
        modifier = Modifier
            .padding(4.dp)
            .height(32.dp)
    ) {
        Box(Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(text, fontSize = 12.sp, fontFamily = customFontFamily)
        }
    }
}

@Composable
fun VendorResultCard(vendor: VendorResponse, customFontFamily: FontFamily) {
    val images = vendor.imageUrls.ifEmpty { listOf("https://via.placeholder.com/300x200.png?text=No+Image") }
    val pagerState = rememberPagerState { images.size }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Vendor Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(0xFF0072C6), shape = RoundedCornerShape(4.dp))
                ) {
                    Text(
                        "4.2 ★ Very Good",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = customFontFamily
                    )
                }
            }

            DotsIndicator(
                totalDots = pagerState.pageCount,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
            )

            Column(Modifier.padding(12.dp)) {
                Text(vendor.businessName, fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${vendor.city} • 2.0 km from center", color = Color.Gray, fontSize = 12.sp, fontFamily = customFontFamily)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹ ${vendor.pricePerBag}", color = Color(0xFF0072C6), fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                    Spacer(Modifier.width(6.dp))
                    Text("₹ 9999", color = Color.Gray, fontSize = 12.sp, textDecoration = TextDecoration.LineThrough, fontFamily = customFontFamily)
                }
                Text("+ ₹ 50 taxes & fees per bag", color = Color.Gray, fontSize = 10.sp, fontFamily = customFontFamily)
                Spacer(Modifier.height(6.dp))
                Text("Secure storage, CCTV, Verified Partner", color = Color.DarkGray, fontSize = 12.sp, fontFamily = customFontFamily)
            }
        }
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.Center, modifier = modifier) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(4.dp)
                    .background(if (index == selectedIndex) Color(0xFF0072C6) else Color.LightGray, CircleShape)
            )
        }
    }
}
