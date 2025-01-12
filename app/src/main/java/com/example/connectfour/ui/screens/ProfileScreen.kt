package com.example.connectfour.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.connectfour.R
import com.example.connectfour.viewmodel.SettingsViewModel

/*
Handles displaying the profile for both players
Separates to two different sides of the screen for each player
Player 2 is either another player or the AI
Default values are given and handled by SettingsViewModel
Stops players from selecting same token color to prevent issues
 */
@Composable
fun ProfileScreen(viewModel: SettingsViewModel = viewModel(), navController: NavHostController) {
    val player1Profile by viewModel.player1Profile.collectAsState()
    val player2Profile by viewModel.player2Profile.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val isLandscape = maxWidth > maxHeight

        Image(
            painter = painterResource(id = R.drawable.main_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.5f))
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profile Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (isLandscape) {
                // Landscape layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PlayerSettingsSection(
                        playerName = player1Profile.name,
                        tokenDrawableRes = player1Profile.tokenDrawableRes,
                        pictureDrawableId = player1Profile.pictureDrawableId,
                        onNameChanged = { name -> viewModel.updatePlayer1Profile(name = name) },
                        onTokenColorSelected = { drawableRes -> viewModel.updatePlayer1Profile(tokenDrawableRes = drawableRes) },
                        onPictureSelected = { pictureDrawableId -> viewModel.updatePlayer1Profile(pictureDrawableId = pictureDrawableId) },
                        unavailableColors = listOf(player2Profile.tokenDrawableRes),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    PlayerSettingsSection(
                        playerName = player2Profile.name,
                        tokenDrawableRes = player2Profile.tokenDrawableRes,
                        pictureDrawableId = player2Profile.pictureDrawableId,
                        onNameChanged = { name -> viewModel.updatePlayer2Profile(name = name) },
                        onTokenColorSelected = { drawableRes -> viewModel.updatePlayer2Profile(tokenDrawableRes = drawableRes) },
                        onPictureSelected = { pictureDrawableId -> viewModel.updatePlayer2Profile(pictureDrawableId = pictureDrawableId) },
                        unavailableColors = listOf(player1Profile.tokenDrawableRes),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                }
            } else {
                // Portrait layout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    PlayerSettingsSection(
                        playerName = player1Profile.name,
                        tokenDrawableRes = player1Profile.tokenDrawableRes,
                        pictureDrawableId = player1Profile.pictureDrawableId,
                        onNameChanged = { name -> viewModel.updatePlayer1Profile(name = name) },
                        onTokenColorSelected = { drawableRes -> viewModel.updatePlayer1Profile(tokenDrawableRes = drawableRes) },
                        onPictureSelected = { pictureDrawableId -> viewModel.updatePlayer1Profile(pictureDrawableId = pictureDrawableId) },
                        unavailableColors = listOf(player2Profile.tokenDrawableRes)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PlayerSettingsSection(
                        playerName = player2Profile.name,
                        tokenDrawableRes = player2Profile.tokenDrawableRes,
                        pictureDrawableId = player2Profile.pictureDrawableId,
                        onNameChanged = { name -> viewModel.updatePlayer2Profile(name = name) },
                        onTokenColorSelected = { drawableRes -> viewModel.updatePlayer2Profile(tokenDrawableRes = drawableRes) },
                        onPictureSelected = { pictureDrawableId -> viewModel.updatePlayer2Profile(pictureDrawableId = pictureDrawableId) },
                        unavailableColors = listOf(player1Profile.tokenDrawableRes)
                    )
                }
            }

            Button(
                onClick = {
                    val finalOneName = if(player1Profile.name.isNullOrBlank()) "Player One" else player1Profile.name
                    val finalTwoName = if(player2Profile.name.isNullOrBlank()) "Player Two" else player2Profile.name
                    viewModel.updatePlayer1Profile(finalOneName)
                    viewModel.updatePlayer2Profile(finalTwoName)
                    navController.navigate("menu")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(35.dp)
            ) {
                Text("Back to Menu")
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

//Displays one players settings
@Composable
fun PlayerSettingsSection(
    playerName: String,
    tokenDrawableRes: Int,
    pictureDrawableId: Int,
    onNameChanged: (String) -> Unit,
    onTokenColorSelected: (Int) -> Unit,
    onPictureSelected: (Int) -> Unit,
    unavailableColors: List<Int> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(4.dp)) {
        TextField(
            value = playerName,
            onValueChange = onNameChanged,
            label = { Text(text = "Enter name") },
            textStyle = TextStyle(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 2.dp)
                .height(46.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Select a token color", fontSize = 16.sp)
                TokenColorOptions(
                    selectedColor = tokenDrawableRes,
                    onColorSelected = onTokenColorSelected,
                    unavailableColors = unavailableColors
                )
            }
            Column {
                Text(text = "Select a picture", fontSize = 16.sp)
                PictureSelectionOptions(
                    selectedPictureDrawableId = pictureDrawableId,
                    onPictureSelected = onPictureSelected
                )
            }
        }
    }
}

//Displays token options
@Composable
fun TokenColorOptions(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    unavailableColors: List<Int> = emptyList()
) {
    val colors = listOf(
        R.drawable.red_token,
        R.drawable.yellow_token,
        R.drawable.green_token,
        R.drawable.pink_token
    )

    Column {
        colors.forEach { colorRes ->
            val isUnavailable = colorRes in unavailableColors
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable(enabled = !isUnavailable) { if (!isUnavailable) onColorSelected(colorRes) }
            ) {
                RadioButton(
                    selected = (colorRes == selectedColor),
                    onClick = { if (!isUnavailable) onColorSelected(colorRes) },
                    enabled = !isUnavailable
                )
                Image(
                    painter = painterResource(id = colorRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

//Displays different profile pictures
@Composable
fun PictureSelectionOptions(selectedPictureDrawableId: Int, onPictureSelected: (Int) -> Unit) {
    val pictures = listOf(
        R.drawable.beans,
        R.drawable.cat,
        R.drawable.cheese,
        R.drawable.cow
    )

    Column {
        pictures.forEach { pictureDrawableId ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (pictureDrawableId == selectedPictureDrawableId),
                    onClick = { onPictureSelected(pictureDrawableId) }
                )
                Image(
                    painter = painterResource(id = pictureDrawableId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

//Simple preview to help design
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    val settingsViewModel = SettingsViewModel()
    ProfileScreen(viewModel = settingsViewModel, navController = navController)
}

//Simple preview to help design
@Preview(showBackground = true,
    device = "spec:width=1029dp,height=480dp")
@Composable
fun ProfileScreenPreviewLandscape() {
    val navController = rememberNavController()
    val settingsViewModel = SettingsViewModel()
    ProfileScreen(viewModel = settingsViewModel, navController = navController)
}