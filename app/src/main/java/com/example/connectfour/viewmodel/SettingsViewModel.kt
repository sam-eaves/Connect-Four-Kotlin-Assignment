package com.example.connectfour.viewmodel

import androidx.lifecycle.ViewModel
import com.example.connectfour.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    // Data class to represent player profile
    data class PlayerProfile(
        val name: String,
        var tokenDrawableRes: Int,  // References drawable resource IDs
        var pictureDrawableId: Int, // Store drawable resource ID directly
        var wins: Int = 0,
        var losses: Int = 0,
    ) {
        //Calculates player win rate
        val winRate: Float
            get() = if (wins + losses > 0) (wins.toFloat() / (wins + losses) * 100) else 0f

        val totalGames: Int
            get() = wins + losses
    }

    //Grid Size defaulting to standard grid size of 6,7
    private val _gridSize = MutableStateFlow(Pair(6, 7))
    val gridSize: StateFlow<Pair<Int, Int>> = _gridSize

    fun setGridSize(rows: Int, cols: Int){
        _gridSize.value = Pair(rows, cols)
    }
    // Helper methods to get rows and columns
    fun getRows(): Int = gridSize.value.first
    fun getColumns(): Int = gridSize.value.second


    //Player 1 defaults with red token and beans picture
    private val _player1Profile = MutableStateFlow(
        PlayerProfile(
            name = "Player One",
            tokenDrawableRes = R.drawable.red_token,
            pictureDrawableId = R.drawable.beans
        )
    )
    val player1Profile: StateFlow<PlayerProfile> = _player1Profile

    //Player 2 defaults to yellow token and cat picture
    private val _player2Profile = MutableStateFlow(
        PlayerProfile(
            name = "Player Two",
            tokenDrawableRes = R.drawable.yellow_token,
            pictureDrawableId = R.drawable.cat
        )
    )
    val player2Profile: StateFlow<PlayerProfile> = _player2Profile

    // Updates player 1 profile, ensuring a valid name, token and picture
    fun updatePlayer1Profile(name: String? = null, tokenDrawableRes: Int? = null, pictureDrawableId: Int? = null) {
        if (name != null) {
            _player1Profile.value = _player1Profile.value.copy(name = name)
        }
        if (tokenDrawableRes != null && tokenDrawableRes != _player2Profile.value.tokenDrawableRes) {
            _player1Profile.value = _player1Profile.value.copy(tokenDrawableRes = tokenDrawableRes)
        }
        if (pictureDrawableId != null) {
            _player1Profile.value = _player1Profile.value.copy(pictureDrawableId = pictureDrawableId)
        }
    }

    // Updates player 2 profile, ensuring a valid name, token and picture
    fun updatePlayer2Profile(name: String? = null, tokenDrawableRes: Int? = null, pictureDrawableId: Int? = null) {
        if (name != null) {
            _player2Profile.value = _player2Profile.value.copy(name = name)
        }
        if (tokenDrawableRes != null && tokenDrawableRes != _player1Profile.value.tokenDrawableRes) {
            _player2Profile.value = _player2Profile.value.copy(tokenDrawableRes = tokenDrawableRes)
        }
        if (pictureDrawableId != null) {
            _player2Profile.value = _player2Profile.value.copy(pictureDrawableId = pictureDrawableId)
        }
    }

    // Handles recording win for either player
    // 1 = player 1, 2 = player 2
    fun recordWin(winner: Int) {
        if (winner == 1) {
            _player1Profile.value = _player1Profile.value.copy(wins = _player1Profile.value.wins + 1)
            _player2Profile.value = _player2Profile.value.copy(losses = _player2Profile.value.losses + 1)
        } else if (winner == 2) {
            _player2Profile.value = _player2Profile.value.copy(wins = _player2Profile.value.wins + 1)
            _player1Profile.value = _player1Profile.value.copy(losses = _player1Profile.value.losses + 1)
        }
    }

    //Leaving function in even though not using
    //Chose not to use to keep persistent game information
    //
    @Suppress("unused")
    fun resetStats() {
        _player1Profile.value = _player1Profile.value.copy(wins = 0, losses = 0)
        _player2Profile.value = _player2Profile.value.copy(wins = 0, losses = 0)
    }
}