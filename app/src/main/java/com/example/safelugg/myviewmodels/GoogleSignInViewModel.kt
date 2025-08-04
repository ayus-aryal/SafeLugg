package com.example.safelugg.myviewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.safelugg.R
import com.example.safelugg.model.CheckUserResponse
import com.example.safelugg.utils.PreferenceHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.security.MessageDigest
import java.util.UUID

class GoogleSignInViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userApiService: UserApiService = UserRetrofitInstance.api


    fun handleGoogleSignIn(context: Context, navController: NavController) {
        viewModelScope.launch {
            googleSignIn(context).collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            val email = user.email ?: ""
                            checkUserExistsAndNavigate(email, context, navController)
                        } else {
                            Toast.makeText(context, "User not found!", Toast.LENGTH_LONG).show()
                        }
                    },
                    onFailure = { e ->
                        Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_LONG)
                            .show()
                        Log.e("GoogleSignIn", "Error: ${e.message}")
                    }
                )
            }
        }
    }

    private suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> = callbackFlow {
        try {
            val credentialManager = CredentialManager.create(context)

            val nonce = UUID.randomUUID().toString()
            val hashedNonce = MessageDigest
                .getInstance("SHA-256")
                .digest(nonce.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                trySend(Result.success(authResult))
            } else {
                throw RuntimeException("Invalid credential type received.")
            }
        } catch (e: GetCredentialCancellationException) {
            trySend(Result.failure(Exception("Sign-in was canceled.")))
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { }
    }


    private suspend fun checkUserExistsAndNavigate(
        email: String,
        context: Context,
        navController: NavController
    ) {
        try {
            val response = userApiService.checkUserExists(email)
            if (response.isSuccessful) {
                val checkUserResponse: CheckUserResponse? = response.body()
                if (checkUserResponse?.exists == true) {
                    PreferenceHelper.setUserLoggedIn(context, true)

                    // User exists → go to home
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                } else {
                    // User does not exist → go to form
                    navController.navigate("fill_your_details") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: HttpException) {
            Toast.makeText(context, "HTTP error: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}