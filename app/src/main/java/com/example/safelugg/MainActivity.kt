package com.example.safelugg

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.safelugg.model.CheckoutVerifyRequest
import com.example.safelugg.myviewmodels.BookingViewModel
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.GoogleSignInViewModel
import com.example.safelugg.myviewmodels.PaymentRetrofitInstance
import com.example.safelugg.myviewmodels.ProvideBookingApi.bookingApi
import com.example.safelugg.myviewmodels.UserRetrofitInstance
import com.example.safelugg.screens.*
import com.example.safelugg.ui.theme.SafeLuggTheme
import com.example.safelugg.utils.PreferenceHelper
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    var lastInitiatedPaymentId: Long? = null
    private val paymentApi = PaymentRetrofitInstance.api

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SafeLugg() }
    }

    // Razorpay success callback
    override fun onPaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData) {
        Log.d("MainActivity", "Razorpay success: $razorpayPaymentId")

        try {
            val json: JSONObject = paymentData.getData()
            val orderId = json.optString("order_id", "")
            val signature = json.optString("signature", "")

            val paymentRecordId = lastInitiatedPaymentId
            if (paymentRecordId == null) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Payment succeeded but payment record not found.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            // Verify payment with backend
            lifecycleScope.launch {
                try {
                    val req = CheckoutVerifyRequest(
                        razorpayOrderId = orderId,
                        razorpayPaymentId = razorpayPaymentId,
                        razorpaySignature = signature
                    )
                    val resp = paymentApi.verifyPayment(paymentRecordId, req)

                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity,
                                "Payment verified successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigate to booking verification screen after success
                            (this@MainActivity as? Activity)?.let { activity ->
                                val navController = (activity as MainActivity).navControllerInstance
                                navController?.navigate("booking_verification_screen/$paymentRecordId")
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Payment verification failed: ${resp.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Verify request failed: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Unexpected payment callback format", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Razorpay error callback
    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        Log.e("MainActivity", "Razorpay error code=$code resp=$response")
        runOnUiThread {
            Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_LONG).show()
        }
    }

    // For navigation access in payment callback
    var navControllerInstance: androidx.navigation.NavHostController? = null
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SafeLugg() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val googleSignInViewModel = GoogleSignInViewModel()
    val customerViewModel: CustomerViewModel = viewModel()
    val userApiService = UserRetrofitInstance.api
    val bookingViewModel: BookingViewModel = viewModel()


    // Expose navController to MainActivity for navigation in callbacks
    (context as? MainActivity)?.navControllerInstance = navController

    SafeLuggTheme {
        NavHost(navController = navController, startDestination = "splash_screen") {

            composable("splash_screen") {
                SplashScreen(navController = navController)
            }

            composable("onboarding_screen") {
                OnboardingScreen(navController)
            }

            composable("welcome_screen") {
                WelcomeScreen {
                    googleSignInViewModel.handleGoogleSignIn(navController.context, navController)
                }
            }

            composable("fill_your_details") {
                FillYourDetailsScreen(navController)
            }

            composable("home_screen") {
                MainScreen(navController, customerViewModel)
            }

            composable(
                route = "search_result_screen/{location}/{date}/{bags}",
                arguments = listOf(
                    navArgument("location") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("bags") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val location = backStackEntry.arguments?.getString("location") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val bags = backStackEntry.arguments?.getString("bags") ?: ""

                SearchResultScreen(
                    location = location,
                    date = date,
                    bags = bags,
                    onEditClick = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() },
                    viewModel = customerViewModel,
                    navController
                )
            }

            composable(
                route = "vendor_details/{vendorId}/{bags}",
                arguments = listOf(
                    navArgument("vendorId") { type = NavType.LongType },
                    navArgument("bags") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.getLong("vendorId") ?: 0
                val bags = backStackEntry.arguments?.getString("bags") ?: ""

                VendorDetailsScreen(
                    vendorId = vendorId,
                    bags = bags,
                    viewModel = viewModel(),
                    bookingApi = bookingApi,
                    onBookingCreated = { /* optional */ }
                )
            }

            // New Booking Verification Screen route
            composable(
                route = "booking_verification_screen/{bookingId}",
                arguments = listOf(navArgument("bookingId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
                BookingConfirmationScreen(
                    viewModel = bookingViewModel,
                    bookingId = bookingId,
                    onClose = { navController.popBackStack() },
                    onDownloadReceipt = { /* implement */ },
                    onDownloadBookingDetails = { /* implement */ },
                    onBackToHome = { navController.navigate("home_screen") },
                    onMyBookings = { /* implement */ }
                )
            }
        }
    }
}
