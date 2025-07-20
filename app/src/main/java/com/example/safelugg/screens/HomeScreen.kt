package com.example.safelugg.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.R
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.CustomerViewModelFactory
import java.util.Calendar

val customFontFamily = FontFamily(Font(R.font.inter))

// Modern color scheme inspired by hotel booking apps
object AppColors {
    val Primary = Color(0xFF1976D2)
    val PrimaryVariant = Color(0xFF1565C0)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF1F5F9)
    val OnSurface = Color(0xFF1E293B)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Outline = Color(0xFFE2E8F0)
}

@Composable
fun MainScreen(navController: NavController, customerViewModel: CustomerViewModel) {
    val context = LocalContext.current
    val viewModelFactory = remember { CustomerViewModelFactory(context) }
    val viewModel: CustomerViewModel = viewModel(factory = viewModelFactory)
    val searchHistory = viewModel.recentSearches

    Scaffold(
        bottomBar = { ModernBottomNavBar(navController) },
        containerColor = AppColors.Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 24.dp
            )
        ) {
            item {
                // Header Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Find Storage",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface,
                        fontFamily = customFontFamily
                    )
                    Text(
                        text = "Secure luggage storage near you",
                        fontSize = 16.sp,
                        color = AppColors.OnSurfaceVariant,
                        fontFamily = customFontFamily
                    )
                }
            }

            item {
                // Modern Search Card
                ModernSearchCard { location, date, bags ->
                    val searchQuery = "$location | $date | $bags"
                    viewModel.addSearchQuery(searchQuery)

                    val encodedLocation = Uri.encode(location)
                    val encodedDate = Uri.encode(date)
                    val encodedBags = Uri.encode(bags)

                    navController.navigate("search_result_screen/$encodedLocation/$encodedDate/$encodedBags")
                }
            }

            item {
                // Recent Searches Section
                ModernRecentSearches(searchHistory = searchHistory) { searchQuery ->
                    // Handle recent search click
                    val parts = searchQuery.split(" | ")
                    if (parts.size == 3) {
                        val encodedLocation = Uri.encode(parts[0])
                        val encodedDate = Uri.encode(parts[1])
                        val encodedBags = Uri.encode(parts[2])
                        navController.navigate("search_result_screen/$encodedLocation/$encodedDate/$encodedBags")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchCard(onSearch: (location: String, date: String, bags: String) -> Unit) {
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var bags by remember { mutableStateOf("2 bags") }
    var isBagDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Location Field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Where",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.OnSurface,
                        fontFamily = customFontFamily
                    )
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = {
                        Text(
                            "Search for a location",
                            color = AppColors.OnSurfaceVariant,
                            fontFamily = customFontFamily
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Outline,
                        focusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Field
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "When",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface,
                            fontFamily = customFontFamily
                        )
                    }
                    OutlinedButton(
                        onClick = { showDatePicker() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, AppColors.Outline)
                    ) {
                        Text(
                            text = date.ifEmpty { "Select date" },
                            color = if (date.isEmpty()) AppColors.OnSurfaceVariant else AppColors.OnSurface,
                            fontFamily = customFontFamily,
                            fontSize = 14.sp
                        )
                    }
                }

                // Bags Field
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bags",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface,
                            fontFamily = customFontFamily
                        )
                    }
                    Box {
                        OutlinedButton(
                            onClick = { isBagDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = AppColors.SurfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, AppColors.Outline)
                        ) {
                            Text(
                                text = bags,
                                color = AppColors.OnSurface,
                                fontFamily = customFontFamily,
                                fontSize = 14.sp
                            )
                        }

                        DropdownMenu(
                            expanded = isBagDropdownExpanded,
                            onDismissRequest = { isBagDropdownExpanded = false }
                        ) {
                            (1..5).forEach { count ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "$count bag${if (count > 1) "s" else ""}",
                                            fontFamily = customFontFamily
                                        )
                                    },
                                    onClick = {
                                        bags = if (count == 1) "1 bag" else "$count bags"
                                        isBagDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Search Button
            Button(
                onClick = { onSearch(location, date, bags) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                enabled = location.isNotEmpty() && date.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search Storage",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = customFontFamily
                )
            }
        }
    }
}

@Composable
fun ModernRecentSearches(
    searchHistory: List<String>,
    onSearchClick: (String) -> Unit
) {
    if (searchHistory.isEmpty()) return

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Recent Searches",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface,
                fontFamily = customFontFamily
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            border = BorderStroke(1.dp, AppColors.Outline)
        ) {
            Column {
                searchHistory.take(5).forEachIndexed { index, searchItem ->
                    val parts = searchItem.split(" | ")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearchClick(searchItem) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = AppColors.OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            if (parts.size >= 3) {
                                Text(
                                    text = parts[0],
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.OnSurface,
                                    fontFamily = customFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${parts[1]} • ${parts[2]}",
                                    fontSize = 14.sp,
                                    color = AppColors.OnSurfaceVariant,
                                    fontFamily = customFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = searchItem,
                                    fontSize = 16.sp,
                                    color = AppColors.OnSurface,
                                    fontFamily = customFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = AppColors.OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (index < searchHistory.lastIndex && index < 4) {
                        HorizontalDivider(
                            color = AppColors.Outline,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Bottom Navigation Bar
data class NavigationItem(val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

@Composable
fun ModernBottomNavBar(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Search", Icons.Outlined.Search, Icons.Filled.Search),
            NavigationItem("Nearby", Icons.Outlined.LocationOn, Icons.Filled.LocationOn),
            NavigationItem("Bookings", Icons.Outlined.DateRange, Icons.Filled.DateRange),
            NavigationItem("Profile", Icons.Outlined.Person, Icons.Filled.Person)
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = customFontFamily,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.Primary,
                    selectedTextColor = AppColors.Primary,
                    unselectedIconColor = AppColors.OnSurfaceVariant,
                    unselectedTextColor = AppColors.OnSurfaceVariant,
                    indicatorColor = AppColors.Primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
