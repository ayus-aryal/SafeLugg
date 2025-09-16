package com.example.safelugg

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
import com.example.safelugg.myviewmodels.CustomerViewModel
import com.example.safelugg.myviewmodels.GoogleSignInViewModel
import com.example.safelugg.myviewmodels.PaymentApi
import com.example.safelugg.myviewmodels.PaymentRetrofitInstance
import com.example.safelugg.myviewmodels.ProvideBookingApi.bookingApi
import com.example.safelugg.myviewmodels.UserRetrofitInstance
import com.example.safelugg.screens.FillYourDetailsScreen
import com.example.safelugg.screens.MainScreen
import com.example.safelugg.screens.OnboardingScreen
import com.example.safelugg.screens.SearchResultScreen
import com.example.safelugg.screens.SplashScreen
import com.example.safelugg.screens.VendorDetailsScreen
import com.example.safelugg.screens.WelcomeScreen
import com.example.safelugg.ui.theme.SafeLuggTheme
import com.example.safelugg.utils.PaymentHandler
import com.example.safelugg.utils.PreferenceHelper
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    // This will be set just before starting checkout.
    // When Razorpay calls back, we will use this id to call /api/payments/{id}/verify
    var lastInitiatedPaymentId: Long? = null

    private val paymentApi = PaymentRetrofitInstance.api

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeLugg()
        }
    }

    // Razorpay success callback
    override fun onPaymentSuccess(razorpayPaymentId: String, paymentData: PaymentData) {
        Log.d("MainActivity", "Razorpay success: $razorpayPaymentId")
        // Extract order_id and signature from paymentData JSON
        try {
            val json: JSONObject = paymentData.getData() // PaymentData.getData() returns JSONObject
            val orderId = json.optString("order_id", "")
            val signature = json.optString("signature", "")

            val paymentRecordId = lastInitiatedPaymentId
            if (paymentRecordId == null) {
                Log.w("MainActivity", "No paymentRecordId set for verification. order=$orderId")
                runOnUiThread {
                    Toast.makeText(this, "Payment succeeded but local payment id unknown", Toast.LENGTH_LONG).show()
                }
                return
            }

            // Call backend verify API
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
                            Toast.makeText(this@MainActivity, "Payment verified successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Payment verified failed: ${resp.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Verify request failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
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
        // Optionally call backend mark failed using lastInitiatedPaymentId
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SafeLugg() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val googleSignInViewModel = GoogleSignInViewModel()
    val customerViewModel: CustomerViewModel = viewModel()  // Shared instance
    val userApiService = UserRetrofitInstance.api             // <-- your Retrofit instance
// <-- you need to store email in SharedPreferences at login



    SafeLuggTheme {
        NavHost(navController = navController, startDestination = "splash_screen") {

            composable(route = "splash_screen") {
                SplashScreen(
                    navController = navController,
                )
            }


            composable(route = "onboarding_screen") {
                OnboardingScreen(navController)
            }

            composable(route = "welcome_screen") {
                WelcomeScreen {
                    googleSignInViewModel.handleGoogleSignIn(navController.context, navController)
                }
            }

            composable(route = "fill_your_details") {
                FillYourDetailsScreen(navController)
            }

            composable(route = "home_screen") {
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
                    onEditClick = {
                        // Navigate to your search/edit screen or popBackStack() if you want to reuse
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = customerViewModel,
                    navController
                )
            }

            composable(
                route = "vendor_details/{vendorId}/{bags}",
                arguments = listOf(
                    navArgument("vendorId") { type = NavType.LongType },
                    navArgument("bags") { type = NavType.StringType } // since you are using String in search

                )
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.getLong("vendorId") ?: 0
                val bags = backStackEntry.arguments?.getString("bags") ?: ""

                VendorDetailsScreen(
                    vendorId = vendorId,
                    bags = bags,
                    viewModel = viewModel(),
                    bookingApi = bookingApi,
                    onBookingCreated = { booking -> /* handle booking */ }
                )            }






        }
    }
}
