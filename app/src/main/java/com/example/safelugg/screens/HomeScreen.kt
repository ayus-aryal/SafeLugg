package com.example.safelugg.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.R
import java.util.Calendar


val customFontFamily = FontFamily(Font(R.font.inter))

@Composable
fun MainScreen(navController: NavController) {
    val searchHistory = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ModernSearchBar { location, date, bags ->
            val searchQuery = "$location | $date | $bags"

            if (searchQuery.isNotBlank() && searchQuery !in searchHistory) {
                searchHistory.add(0, searchQuery)
            }

            // Navigate to SearchResultScreen with arguments
            val encodedLocation = Uri.encode(location)
            val encodedDate = Uri.encode(date)
            val encodedBags = Uri.encode(bags)

            navController.navigate("search_result_screen/$encodedLocation/$encodedDate/$encodedBags")
        }


        RecentSearchCard(searchHistory)
        BottomNavBar(navController)
    }
}

// Top Search Bar
// --- Inside your ModernSearchBar ---

@Composable
fun ModernSearchBar(onSearch: (location: String, date: String, bags: String) -> Unit) {
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Date") }
    var bags by remember { mutableStateOf("2 bags") }
    var isBagDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 10.dp, end = 10.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Icon Button (Clickable)
            IconButton(onClick = {
                onSearch(location, date, bags)
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Location Field
            TextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Location", fontFamily = customFontFamily) },
                singleLine = true,
                modifier = Modifier.weight(1.5f),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            VerticalDivider()

            // Date Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.2f)
            ) {
                IconButton(onClick = { showDatePicker { date = it } }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Date")
                }
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = customFontFamily,
                    color = Color.DarkGray
                )
            }

            VerticalDivider()

            // Bag Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.3f)
            ) {
                Box {
                    IconButton(onClick = { isBagDropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Select Bags"
                        )
                    }

                    DropdownMenu(
                        expanded = isBagDropdownExpanded,
                        onDismissRequest = { isBagDropdownExpanded = false }
                    ) {
                        (1..5).forEach { count ->
                            DropdownMenuItem(
                                text = { Text("$count bag${if (count > 1) "s" else ""}") },
                                onClick = {
                                    bags = if (count == 1) "1 bag" else "$count bags"
                                    isBagDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = bags,
                    fontWeight = FontWeight.Light,
                    fontFamily = customFontFamily,
                    color = Color.DarkGray
                )
            }
        }
    }
}



@Composable
fun VerticalDivider() {
    Spacer(modifier = Modifier.width(10.dp))
    Divider(
        color = Color.Gray,
        modifier = Modifier
            .height(24.dp)
            .width(1.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
}


// Recent Searches Card
@Composable
fun RecentSearchCard(searchHistory: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(2.dp, Color(0xFFB0B0B0)),

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Recent Searches",
                    tint = Color(0xFF6A5ACD),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Recent Searches",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A),
                    fontFamily = customFontFamily
                )
            }

            if (searchHistory.isEmpty()) {
                Text(
                    "No recent searches",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontFamily = customFontFamily

                )
            } else {
                searchHistory.forEachIndexed { index, searchItem ->
                    Column {
                        Text(
                            searchItem,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        if (index < searchHistory.lastIndex) {
                            Divider(color = Color(0xFFB0B0B0))
                        }
                    }
                }
            }
        }
    }
}

// Bottom Navigation Bar
data class NavigationItem(val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            NavigationItem("Browse", Icons.Default.Search),
            NavigationItem("Nearby", Icons.Default.Place),
            NavigationItem("Bookings", Icons.Default.DateRange),
            NavigationItem("Menu", Icons.Default.MoreVert)
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selectedTab == index) Color(0xFF0072C6) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selectedTab == index) Color(0xFF0072C6) else Color.Gray,
                        fontFamily = customFontFamily

                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    val navController = rememberNavController()
    MainScreen(navController)
}
