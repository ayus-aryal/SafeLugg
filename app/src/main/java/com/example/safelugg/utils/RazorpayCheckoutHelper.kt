package com.example.safelugg.utils

import android.app.Activity
import android.util.Log
import com.razorpay.Checkout
import org.json.JSONObject

object RazorpayCheckoutHelper {
    /**
     * Start Razorpay checkout.
     *
     * @param activity the Activity (must implement payment callbacks via PaymentResultWithDataListener).
     * @param keyId Razorpay key id (test key, publishable id).
     * @param orderId Razorpay order id returned by your backend
     * @param amountPaise amount in paise (Integer)
     * @param name merchant/app name
     */
    fun startCheckout(activity: Activity, keyId: String, orderId: String, amountPaise: Int, name: String = "SafeLugg") {
        val co = Checkout()
        Log.d("Razorpay", "Key being used: $keyId")

        co.setKeyID(keyId)

        val options = JSONObject().apply {
            put("name", name)
            put("description", "Booking Payment")
            put("order_id", orderId)       // mandatory if you created order server-side
            put("currency", "INR")
            put("amount", amountPaise)     // paise
            // you can add prefill, theme etc here
        }

        co.open(activity, options)
    }
}