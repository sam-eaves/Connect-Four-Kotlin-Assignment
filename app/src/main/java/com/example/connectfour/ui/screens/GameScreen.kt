package com.example.connectfour.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.connectfour.R
import com.example.connectfour.viewmodel.SettingsViewModel
import com.example.connectfour.viewmodel.GameViewModel

/*
GameScreen handles displaying the game UI
Handles both orientations, changing the UI depending on view
 */
@Composable
fun GameScreen(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    isVsAI: Boolean
) {
    val gameState by gameViewModel.gameState.collectAsState()

    // Extract necessary values from gameState
    val boardState = gameState.board
    val message = gameState.message
    val moveCount = gameState.moveCount
    val remainingMoves = gameState.remainingMoves

    // Observe player profiles from SettingsViewModel
    val player1Profile by settingsViewModel.player1Profile.collectAsState()
    val player2Profile by settingsViewModel.player2Profile.collectAsState()

    //Retrieves rows and cols for board creation
    val rows = settingsViewModel.getRows()
    val cols = settingsViewModel.getColumns()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        //Gives consistent background
        Image(
            painter = painterResource(id = R.drawable.main_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (maxWidth < maxHeight) {
            // Portrait Mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f)) //Gives brighter contrast
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PlayerInfoSectionRow(player1Profile, player2Profile)
                GameBoard(
                    boardState = boardState,
                    rows = rows,
                    cols = cols,
                    gameViewModel = gameViewModel,
                    player1Profile = player1Profile,
                    player2Profile = player2Profile,
                    isVsAI = isVsAI,
                    settingsViewModel = settingsViewModel
                )
                GameControls(
                    navController,
                    gameViewModel,
                    rows, cols,
                    moveCount,
                    remainingMoves,
                    totalGames = player1Profile.totalGames,
                    message,
                    isVsAI
                )
            }
        } else {
            // Landscape Mode
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f)) //Brighter contrast
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerInfoSection(
                        name = player1Profile.name,
                        pictureDrawableId = player1Profile.pictureDrawableId,
                        wins = player1Profile.wins,
                        losses = player1Profile.losses,
                        winRate = player1Profile.winRate,
                        tokenDrawableRes = player1Profile.tokenDrawableRes
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    GameControlButtons(gameViewModel, rows, cols, isVsAI)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            gameViewModel.restartGame(rows, cols)
                            navController.navigate("menu")
                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text("Back to Menu")
                    }
                }
                GameBoard(
                    boardState = boardState,
                    rows = rows,
                    cols = cols,
                    gameViewModel = gameViewModel,
                    player1Profile = player1Profile,
                    player2Profile = player2Profile,
                    isVsAI = isVsAI,
                    settingsViewModel = settingsViewModel
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerInfoSection(
                        name = player2Profile.name,
                        pictureDrawableId = player2Profile.pictureDrawableId,
                        wins = player2Profile.wins,
                        losses = player2Profile.losses,
                        tokenDrawableRes = player2Profile.tokenDrawableRes,
                        winRate = player2Profile.winRate
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    GameMessage(message)
                }
            }
        }
    }
}

/*
Contains all player related info
Uses SettingsViewModel to access both profiles
Updates wins and losses dynamically during play.
 */
@Composable
fun PlayerInfoSectionRow(player1Profile: SettingsViewModel.PlayerProfile, player2Profile: SettingsViewModel.PlayerProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        //Player 1 section
        PlayerInfoSection(
            name = player1Profile.name,
            pictureDrawableId = player1Profile.pictureDrawableId,
            wins = player1Profile.wins,
            losses = player1Profile.losses,
            winRate = player1Profile.winRate,
            tokenDrawableRes = player1Profile.tokenDrawableRes

        )
        //Player 2 section
        PlayerInfoSection(
            name = player2Profile.name,
            pictureDrawableId = player2Profile.pictureDrawableId,
            wins = player2Profile.wins,
            losses = player2Profile.losses,
            winRate = player2Profile.winRate,
            tokenDrawableRes = player2Profile.tokenDrawableRes
        )
    }
}

//Handles displaying GameBoard and placing tokens into the grid as clickable boxes
@Composable
fun GameBoard(
    boardState: Array<IntArray>,
    rows: Int,
    cols: Int,
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
    player1Profile: SettingsViewModel.PlayerProfile,
    player2Profile: SettingsViewModel.PlayerProfile,
    isVsAI: Boolean
) {
    val gameState by gameViewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .aspectRatio(cols.toFloat() / rows.toFloat())
            .background(Color.Blue)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        //This logic for column and rows handles spacing the boxes evenly for tokens
        Column {
            repeat(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(cols) { col ->
                        //Acts as a clickable in the connect four grid for token placement
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                //This prevents button clicks after a game is over
                                .clickable(enabled = !gameState.isGameOver) {
                                    gameViewModel.makeMove(col, isVsAI, settingsViewModel)
                                }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            //Number dictates what token is placed
                            //0 = empty, 1 = player 1, 2 = player 2
                            when (boardState[row][col]) {
                                0 -> Image(
                                    painter = painterResource(id = R.drawable.empty_token),
                                    contentDescription = "Empty Token",
                                    modifier = Modifier.fillMaxSize()
                                )
                                1 -> Image(
                                    painter = painterResource(id = player1Profile.tokenDrawableRes),
                                    contentDescription = "Player 1 Token",
                                    modifier = Modifier.fillMaxSize()
                                )
                                2 -> Image(
                                    painter = painterResource(id = player2Profile.tokenDrawableRes),
                                    contentDescription = "Player 2 Token",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//Handles displaying game control buttons and extra game information display
@Composable
fun GameControls(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    rows: Int,
    cols: Int,
    moveCount: Int,
    remainingMoves: Int,
    totalGames: Int,
    message: String,
    isVsAI: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameControlButtons(gameViewModel, rows, cols, isVsAI)
        Text(
            text = "Moves: $moveCount, Remaining: $remainingMoves",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Total games played: $totalGames",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        GameMessage(message)
        Button(
            onClick = {
                gameViewModel.restartGame(rows, cols)
                navController.navigate("menu")
            },
            modifier = Modifier
                .width(150.dp)
                .padding(vertical = 8.dp)
        ) {
            Text("Back to Menu")
        }
    }
}

// Undo and restart buttons
@Composable
fun GameControlButtons(gameViewModel: GameViewModel, rows: Int, cols: Int, isVsAI: Boolean) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            //If AI undo twice to go back to player move
            if (isVsAI) {
                gameViewModel.undoMove()
                gameViewModel.undoMove()

                //Ensures it reverts back to player 1
                gameViewModel.setCurrentPlayer(1)
            }

            //Otherwise undo once since player vs player
            else {
                gameViewModel.undoMove()
            }
        },
            modifier = Modifier.height(37.dp)
        ) {
            Text("Undo", fontSize = 13.sp)
        }
        Button(
            onClick = { gameViewModel.restartGame(rows, cols) },
            modifier = Modifier.height(37.dp)
        ) {
            Text("Restart", fontSize = 13.sp)
        }
    }
}

//Displays on win, lose or draw
@Composable
fun GameMessage(message: String) {
    Text(
        text = message,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Red,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// Displays a single players information from SettingsViewModel
@Composable
fun PlayerInfoSection(
    name: String,
    pictureDrawableId: Int,
    wins: Int,
    losses: Int,
    tokenDrawableRes: Int,
    winRate: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)

    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = pictureDrawableId),
            contentDescription = name,
            modifier = Modifier.size(60.dp)
        )
        Text(
            text = "Wins: $wins, Losses: $losses",
            fontSize = 16.sp
        )
        Text(
            text = "Win rate: ${"%.2f".format(winRate)}"
        )
        Image(
            painter = painterResource(id = tokenDrawableRes),
            contentDescription = "Token",
            modifier = Modifier.size(30.dp)
        )
    }
}

//Simple preview to allow for easy design
@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    val settingsViewModel = SettingsViewModel()
    val gameViewModel = GameViewModel()
    GameScreen(navController = navController, gameViewModel = gameViewModel, settingsViewModel = settingsViewModel, isVsAI = false)
}

@Preview(
    showBackground = true,
    widthDp = 1029,
    heightDp = 480
)
@Composable
fun LandscapePreview() {
    val navController = rememberNavController()
    val settingsViewModel = SettingsViewModel()
    val gameViewModel = GameViewModel()
    val rows = 5
    val cols = 6
    settingsViewModel.setGridSize(rows, cols)
    GameScreen(navController = navController, gameViewModel = gameViewModel, settingsViewModel = settingsViewModel, isVsAI = false)
}