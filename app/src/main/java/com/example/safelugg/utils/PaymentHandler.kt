package com.example.safelugg.utils

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.safelugg.model.CheckoutVerifyRequest
import com.example.safelugg.myviewmodels.PaymentRetrofitInstance
import com.razorpay.PaymentData
import kotlinx.coroutines.launch

object PaymentHandler {

    fun handleSuccess(activity: Activity, razorpayPaymentId: String, paymentData: PaymentData) {
        val orderId = paymentData.orderId
        val signature = paymentData.signature

        val localPaymentId = PendingPaymentStore.orderToLocalPayment[orderId]
        if (localPaymentId == null) {
            Toast.makeText(activity, "Payment complete, but local record missing.", Toast.LENGTH_LONG).show()
            return
        }

        (activity as? ComponentActivity)?.lifecycleScope?.launch {
            try {
                val req = CheckoutVerifyRequest(
                    razorpayOrderId = orderId,
                    razorpayPaymentId = razorpayPaymentId,
                    razorpaySignature = signature
                )

                val resp = PaymentRetrofitInstance.api.verifyPayment(localPaymentId, req)
                if (resp.isSuccessful) {
                    Toast.makeText(activity, "Payment verified ✔", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Verify failed: ${resp.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Error verifying payment: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                PendingPaymentStore.orderToLocalPayment.remove(orderId)
            }
        }
    }

    fun handleError(activity: Activity, code: Int, response: String, paymentData: PaymentData?) {
        paymentData?.orderId?.let { PendingPaymentStore.orderToLocalPayment.remove(it) }
        Toast.makeText(activity, "Payment failed: $response", Toast.LENGTH_LONG).show()
    }
}