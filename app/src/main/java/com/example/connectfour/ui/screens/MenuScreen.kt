package com.example.connectfour.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connectfour.R
import com.example.connectfour.viewmodel.GameViewModel
import com.example.connectfour.viewmodel.SettingsViewModel

/*
Handles displaying Menu buttons
App starting point
 */
@Composable
fun MenuScreen(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel
) {
    // Box allows stacking the background image with the content layered on top
    Box(
        modifier = Modifier.fillMaxSize()  // Fills the entire screen
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.main_background),  // Replace with your image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop  // Scale the image to fill the entire screen
        )

        // Foreground content (Buttons, Text, etc.)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Connect Four",
                fontSize = 30.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Initializes board ensuring correct rows and cols then navigates to game screen
            Button(
                onClick = {
                    gameViewModel.initializeGame(
                        rows = settingsViewModel.getRows(),
                        cols = settingsViewModel.getColumns()
                    )
                    navController.navigate("game_vs_friend") // Navigate directly to "game_vs_friend"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Play vs Friend!")
            }

            // Initializes board ensuring correct rows and cols then navigates to game screen
            Button(
                onClick = {
                    gameViewModel.initializeGame(
                        rows = settingsViewModel.getRows(),
                        cols = settingsViewModel.getColumns()
                    )
                    navController.navigate("game_vs_ai") // Navigate directly to "game_vs_ai"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Play vs AI!")
            }

            // Navigates to Profile Screen
            Button(
                onClick = { navController.navigate("profile") },  // Navigate to ProfileScreen
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Profile!")
            }

            // Navigates to Settings Screen
            Button(
                onClick = { navController.navigate("settings") },  // Navigate to SettingsScreen
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Settings!")
            }
        }
    }
}


//Simple preview for easy design
@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    val navController = rememberNavController()
    val settingsViewModel: SettingsViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()
    MenuScreen(navController, gameViewModel, settingsViewModel)
}

