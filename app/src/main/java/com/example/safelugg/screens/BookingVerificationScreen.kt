package com.example.safelugg.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safelugg.myviewmodels.BookingViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// Modern color palette
private val PrimaryGreen = Color(0xFF10B981)
private val LightGreen = Color(0xFFD1FAE5)
private val DarkGray = Color(0xFF1F2937)
private val MediumGray = Color(0xFF6B7280)
private val LightGray = Color(0xFFF3F4F6)
private val CardBackground = Color(0xFFFFFFFF)


@RequiresApi(Build.VERSION_CODES.O)
fun formatOffsetDateTime(odt: OffsetDateTime?): String {
    return if (odt == null) {
        "N/A"
    } else {
        try {
            odt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
        } catch (e: Exception) {
            "N/A"
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BookingConfirmationScreen(
    viewModel: BookingViewModel,
    bookingId: Long,
    onClose: () -> Unit = {},
    onDownloadReceipt: () -> Unit = {},
    onDownloadBookingDetails: () -> Unit = {},
    onBackToHome: () -> Unit = {},
    onMyBookings: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }

    val bookingVerification by viewModel.bookingVerification
    val isLoading by viewModel.isLoading

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    // Fetch booking verification once on first composition
    LaunchedEffect(bookingId) {
        isVisible = true
        viewModel.fetchBookingVerification(bookingId)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        val isCompact = maxWidth < 600.dp
        val horizontalPadding = if (isCompact) 16.dp else 24.dp
        val cardSpacing = if (isCompact) 12.dp else 16.dp

        // Safe formatted date strings


        if (isLoading) {
            // Show loading indicator while fetching data
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (bookingVerification == null) {
            // Show placeholder or error if data not available
            Text(
                text = "Booking details not available",
                modifier = Modifier.align(Alignment.Center),
                color = DarkGray
            )
        } else {
            // Safe strings for dates
            val checkInText = formatOffsetDateTime(bookingVerification?.checkIn)
            val checkOutText = formatOffsetDateTime(bookingVerification?.checkOut)


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top section with gradient and success animation
                AnimatedSuccessHeader(isVisible)

                // Content cards with staggered animation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(cardSpacing)
                ) {
                    AnimatedCard(delay = 100) {
                        BookingIdCard(bookingVerification?.bookingId ?: "N/A")                    }

                    AnimatedCard(delay = 200) {
                        VendorDetailsCard(
                            businessName = bookingVerification?.businessName ?: "N/A",
                            address = bookingVerification?.address ?: "N/A",
                            phoneNumber = bookingVerification?.phoneNumber ?: "N/A"
                        )
                    }

                    AnimatedCard(delay = 300) {
                        BookingDetailsCard(
                            checkIn = checkInText,
                            checkOut = checkOutText,
                            durationHours = bookingVerification?.durationHours ?: 0,
                            noOfBags = bookingVerification?.noOfBags ?: 0
                        )
                    }

                    AnimatedCard(delay = 400) {
                        PaymentDetailsCard(
                            paidAmount = bookingVerification?.paidAmount ?: 0,
                            paymentMode = bookingVerification?.paymentMode ?: "N/A",
                            paymentId = bookingVerification?.paymentId ?: "N/A",
                            paymentStatus = bookingVerification?.paymentStatus ?: "N/A"
                        )
                    }

                    AnimatedCard(delay = 500) {
                        PoliciesCard()
                    }

                    AnimatedCard(delay = 600) {
                        ActionButtons(
                            onDownloadReceipt = onDownloadReceipt,
                            onDownloadBookingDetails = onDownloadBookingDetails,
                            onBackToHome = onBackToHome,
                            onMyBookings = onMyBookings
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


@Composable
private fun AnimatedSuccessHeader(isVisible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
            .padding(vertical = 48.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .background(PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Booking Confirmed!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Your luggage storage is all set",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MediumGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedCard(
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BookingIdCard(bookingId: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightGreen, CardBackground)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.ConfirmationNumber,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Booking ID",
                style = MaterialTheme.typography.bodyMedium,
                color = MediumGray,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = bookingId ?: "Loading...",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = DarkGray
        )
    }
}


@Composable
private fun VendorDetailsCard(
    businessName: String?,
    address: String?,
    phoneNumber: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        SectionHeader("Storage Location", Icons.Default.LocationOn)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = businessName ?: "Loading...",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoRow(Icons.Default.Place, address ?: "Loading...")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(Icons.Default.Phone, phoneNumber ?: "Loading...")
    }
}


@Composable
private fun BookingDetailsCard(
    checkIn: String?,
    checkOut: String?,
    durationHours: Int?,
    noOfBags: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        SectionHeader("Booking Details", Icons.Default.CalendarToday)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailColumn(
                icon = Icons.Default.Login,
                label = "Check-in",
                value = checkIn ?: "Loading...",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            DetailColumn(
                icon = Icons.Default.Logout,
                label = "Check-out",
                value = checkOut ?: "Loading...",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailColumn(
                icon = Icons.Default.AccessTime,
                label = "Duration",
                value = durationHours?.let { "$it hrs" } ?: "Loading...",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            DetailColumn(
                icon = Icons.Default.Work,
                label = "Bags",
                value = noOfBags?.let { "$it bags" } ?: "Loading...",
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun PaymentDetailsCard(
    paidAmount: Int?,
    paymentMode: String?,
    paymentId: String?,
    paymentStatus: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        SectionHeader("Payment Details", Icons.Default.Payment)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGreen, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Amount Paid",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = DarkGray
            )
            Text(
                text = paidAmount?.let { "₹$it" } ?: "Loading...",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PaymentInfoRow("Payment Mode", paymentMode ?: "Loading...")
        Spacer(modifier = Modifier.height(12.dp))
        PaymentInfoRow("Transaction ID", paymentId ?: "Loading...")
        Spacer(modifier = Modifier.height(12.dp))
        PaymentInfoRow(
            "Payment Status",
            paymentStatus ?: "Loading...",
            isHighlighted = true
        )
    }
}


@Composable
private fun PoliciesCard() {
    val policies = listOf(
        "Free cancellation up to 2 hours before check-in" to Icons.Default.Cancel,
        "Items must be properly tagged and secured" to Icons.Default.Label,
        "Maximum weight: 25kg per bag" to Icons.Default.FitnessCenter,
        "Prohibited: Electronics, Liquids, Food items" to Icons.Default.Block
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        SectionHeader("Important Policies", Icons.Default.Policy)

        Spacer(modifier = Modifier.height(16.dp))

        policies.forEach { (policy, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = policy,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray,
                    modifier = Modifier.weight(1f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onDownloadReceipt: () -> Unit,
    onDownloadBookingDetails: () -> Unit,
    onBackToHome: () -> Unit,
    onMyBookings: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onDownloadBookingDetails,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Download Booking Details",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        OutlinedButton(
            onClick = onDownloadReceipt,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DarkGray
            ),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Download Receipt",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onMyBookings,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "My Bookings",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            OutlinedButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

// Helper Composables
@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DarkGray
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MediumGray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkGray
        )
    }
}

@Composable
private fun DetailColumn(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(LightGray, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MediumGray,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DarkGray
        )
    }
}

@Composable
private fun PaymentInfoRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MediumGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = if (isHighlighted) PrimaryGreen else DarkGray
        )
    }
}
