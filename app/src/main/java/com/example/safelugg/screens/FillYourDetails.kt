package com.example.safelugg.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelugg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FillYourDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            FillYourDetailsScreen(navController)
        }
    }
}

@Composable
fun FillYourDetailsScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val userEmail = auth.currentUser?.email

    val isFormValid by remember {
        derivedStateOf {
            firstName.isNotBlank() &&
                    lastName.isNotBlank() &&
                    phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
        }
    }

    val customFontFamily = FontFamily(Font(R.font.inter)) // Replace 'inter' with your actual font file

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Complete your profile",
            fontSize = 32.sp,
            fontFamily = customFontFamily,
            modifier = Modifier.padding(bottom = 16.dp, top = 45.dp),
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "First name",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                fontFamily = customFontFamily,
                modifier = Modifier.padding(start = 16.dp)
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Last name",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                fontFamily = customFontFamily,
                modifier = Modifier.padding(start = 16.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Phone Number",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                fontFamily = customFontFamily,
                modifier = Modifier.padding(start = 16.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("home_screen") }
            ) {
                Text("Skip for now", fontFamily = customFontFamily)
            }

            Button(
                onClick = {
                    if (userId != null && userEmail != null) {
                        saveUserDataToFirestore(userId, firstName, lastName, userEmail, phoneNumber, firestore, context, navController)
                    } else {
                        Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isFormValid
            ) {
                Text("Save", fontFamily = customFontFamily)
            }
        }
    }
}

// 🔹 Function to Save Data to Firestore
fun saveUserDataToFirestore(
    userId: String,
    firstName: String,
    lastName: String,
    email: String,
    phoneNumber: String,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    navController: NavController
) {
    val userMap = hashMapOf(
        "firstName" to firstName,
        "lastName" to lastName,
        "email" to email,
        "phoneNumber" to phoneNumber
    )

    firestore.collection("users").document(userId)
        .set(userMap)
        .addOnSuccessListener {
            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate("home_screen")
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showBackground = true)
@Composable
fun FillYourDetailsScreenPreview() {
    val navController = rememberNavController()
    FillYourDetailsScreen(navController)
}
