package com.example.safelugg.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelugg.myviewmodels.CustomerViewModel

@Composable
fun SearchResultScreen(viewModel: CustomerViewModel = viewModel()) {

    val apiResult by viewModel.apiResponse

    LaunchedEffect(Unit) {
        viewModel.fetchApiResponse()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "API Response:")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = apiResult)
    }
}
