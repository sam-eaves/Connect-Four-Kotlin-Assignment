package com.example.connectfour.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.connectfour.viewmodel.SettingsViewModel
import com.example.connectfour.R

/*
Simple settings screen, only gives options for different grid sizes
Separates profile screen and settings this way
Double colon references function allowing it to be passed around
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(), navController: NavHostController) {
    val currentGridSize = viewModel.gridSize.collectAsState()
    val (rows, cols) = currentGridSize.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.main_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select Game Grid Size",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grid Size Options
            GridSizeOption(
                label = "Standard (7x6)",
                gridSize = Pair(6, 7),
                selectedGridSize = Pair(rows, cols),
                onGridSizeSelected = viewModel::setGridSize
            )

            GridSizeOption(
                label = "Small (6x5)",
                gridSize = Pair(5, 6),
                selectedGridSize = Pair(rows, cols),
                onGridSizeSelected = viewModel::setGridSize
            )

            GridSizeOption(
                label = "Large (8x7)",
                gridSize = Pair(7, 8),
                selectedGridSize = Pair(rows, cols),
                onGridSizeSelected = viewModel::setGridSize
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Back to Menu Button
            Button(
                onClick = { navController.navigate("menu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Back to Menu")
            }
        }
    }
}

/*
Displays different grid sizes
Selectable by radiobutton
 */
@Composable
fun GridSizeOption(
    label: String,
    gridSize: Pair<Int, Int>,  // Using gridSize directly
    selectedGridSize: Pair<Int, Int>,  // Using gridSize directly for comparison
    onGridSizeSelected: (Int, Int) -> Unit  // This function expects two Ints, rows and cols
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onGridSizeSelected(gridSize.first, gridSize.second) }
    ) {
        RadioButton(
            selected = selectedGridSize == gridSize,
            onClick = { onGridSizeSelected(gridSize.first, gridSize.second) }
        )
        Text(
            text = label,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

//Simple preview to help design
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()
    val settingsViewModel = SettingsViewModel()
    SettingsScreen(settingsViewModel, navController) // No need to pass 'onGridSizeSelected' because it has a default value
}
