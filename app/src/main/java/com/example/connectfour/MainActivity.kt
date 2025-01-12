package com.example.connectfour

import com.example.connectfour.ui.screens.*
import com.example.connectfour.viewmodel.SettingsViewModel
import com.example.connectfour.viewmodel.GameViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Just calls MainApp and runs
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

/*
Handles initializing and navigation.
Main starting point for entire app
 */
@Composable
fun MainApp() {
    // Set up navigation controller
    val navController = rememberNavController()

    // Initialize ViewModels
    val settingsViewModel: SettingsViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(navController = navController, gameViewModel, settingsViewModel)
        }
        composable("profile") {
            ProfileScreen(viewModel = settingsViewModel, navController = navController)
        }
        composable("settings") {
            SettingsScreen(viewModel = settingsViewModel, navController = navController)
        }
        composable("game_vs_friend") {
            GameScreen(
                navController = navController,
                gameViewModel = gameViewModel,
                settingsViewModel = settingsViewModel,
                isVsAI = false // Explicitly set to false for "Play vs Friend"
            )
        }
        composable("game_vs_ai") {
            GameScreen(
                navController = navController,
                gameViewModel = gameViewModel,
                settingsViewModel = settingsViewModel,
                isVsAI = true // Explicitly set to true for "Play vs AI"
            )
        }
    }
}
