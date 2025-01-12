package com.example.connectfour.viewmodel

import androidx.lifecycle.ViewModel
import com.example.connectfour.model.MCTS_AI
import com.example.connectfour.model.Move
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    // Data class to represent the game state
    data class GameState(
        val board: Array<IntArray>,
        val currentPlayer: Int,
        val moveHistory: List<Pair<Int, Int>>,
        val moveCount: Int,
        val remainingMoves: Int,
        val message: String,
        val isGameOver: Boolean
    )

    // Default values for game state
    private val _gameState = MutableStateFlow(
        GameState(
            board = Array(6) { IntArray(7) },  // Standard 6x7 Connect Four board
            currentPlayer = 1,
            moveHistory = emptyList(),
            moveCount = 0,
            remainingMoves = 6 * 7,
            message = "",
            isGameOver = false
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    // Initialize the game board based on the grid size
    fun initializeGame(rows: Int, cols: Int) {
        _gameState.value = GameState(
            board = Array(rows) { IntArray(cols) },
            currentPlayer = 1,
            moveHistory = emptyList(),
            moveCount = 0,
            remainingMoves = rows * cols,
            message = "",
            isGameOver = false
        )
    }

    fun makeMove(col: Int, isVsAI: Boolean, settingsViewModel: SettingsViewModel) {
        val currentGameState = _gameState.value
        val board = currentGameState.board

        if (currentGameState.isGameOver) {
            return
        }

        // Find the first empty row in the selected column
        for (row in board.indices.reversed()) {
            if (board[row][col] == 0) {
                board[row][col] = currentGameState.currentPlayer
                val newMoveHistory = currentGameState.moveHistory + Pair(row, col)

                // Check for a win or draw
                val newMessage = when {
                    checkWinCondition(board, currentGameState.currentPlayer, board.size, board[0].size) -> {
                        // Record the win using SettingsViewModel
                        settingsViewModel.recordWin(currentGameState.currentPlayer)
                        "${if (currentGameState.currentPlayer == 1) "Player 1" else "Player 2"} Wins!"
                    }
                    newMoveHistory.size == board.size * board[0].size -> {
                        // Draw condition
                        "Draw!"
                    }
                    else -> ""
                }

                val isGameOver = newMessage.isNotEmpty()

                // Update state with the new board, move history, and result
                _gameState.value = currentGameState.copy(
                    board = board,
                    moveHistory = newMoveHistory,
                    moveCount = currentGameState.moveCount + 1,
                    remainingMoves = currentGameState.remainingMoves - 1,
                    message = newMessage,
                    isGameOver = isGameOver
                )

                // Check if the game should continue or if AI should take its turn
                if (newMessage.isEmpty() && isVsAI && !isGameOver) {
                    switchPlayer()  // Switch to AI
                    makeAiMove(board, settingsViewModel)
                } else if (!isGameOver) {
                    switchPlayer()  // Switch to Player 2
                }
                break
            }
        }
    }

    // AI move logic separated but still using the updated state
    private fun makeAiMove(board: Array<IntArray>, settingsViewModel: SettingsViewModel) {
        val currentGameState = _gameState.value

        // Create an instance of MCTS_AI, passing the current game state
        val mctsAI = MCTS_AI(currentGameState, this)

        // Find the best move using MCTS
        val bestMove = mctsAI.findBestMove()

        if (bestMove != null) {
            // Perform the move in the selected column
            for (row in board.indices.reversed()) {
                if (board[row][bestMove.column] == 0) {
                    board[row][bestMove.column] = 2  // AI is always Player 2
                    val newMoveHistory = currentGameState.moveHistory + Pair(row, bestMove.column)

                    // Check for a win or draw for the AI
                    val newMessage = when {
                        checkWinCondition(board, 2, board.size, board[0].size) -> {
                            settingsViewModel.recordWin(2)
                            "Player 2 (AI) Wins!"
                        }
                        newMoveHistory.size == board.size * board[0].size -> "Draw!"
                        else -> ""
                    }

                    // Update state for AI move
                    _gameState.value = currentGameState.copy(
                        board = board,
                        moveHistory = newMoveHistory,
                        moveCount = currentGameState.moveCount + 1,
                        remainingMoves = currentGameState.remainingMoves - 1,
                        message = newMessage,
                        isGameOver = newMessage.isNotEmpty()
                    )

                    // Switch back to Player 1 if game is not over
                    if (newMessage.isEmpty()) {
                        switchPlayer()
                    }
                    break
                }
            }
        }
    }


    // Switches the current player between Player 1 and Player 2 (AI)
    private fun switchPlayer() {
        val currentGameState = _gameState.value
        _gameState.value = currentGameState.copy(
            currentPlayer = if (currentGameState.currentPlayer == 1) 2 else 1
        )
    }

    // Simple undo function
    fun undoMove() {
        val currentGameState = _gameState.value
        val moveHistory = currentGameState.moveHistory

        if (moveHistory.isNotEmpty()) {
            val lastMove = moveHistory.last()
            val board = currentGameState.board

            // Remove the last move from the board
            board[lastMove.first][lastMove.second] = 0

            // Determine the correct player after undo
            val previousPlayer = currentGameState.currentPlayer  // Keep the current player

            // Update the game state after undo
            _gameState.value = currentGameState.copy(
                board = board,
                moveHistory = moveHistory.dropLast(1),
                moveCount = currentGameState.moveCount - 1,
                remainingMoves = currentGameState.remainingMoves + 1,
                currentPlayer = previousPlayer,  // Do not switch the player
                message = "Move undone",
                isGameOver = false  // Allow the game to continue
            )
        } else {
            // If no moves to undo, set a message
            _gameState.value = currentGameState.copy(message = "No moves to undo")
        }
    }


    // Set the current player (used after undoing AI moves)
    fun setCurrentPlayer(player: Int) {
        _gameState.value = _gameState.value.copy(currentPlayer = player)
    }

    // Restart the game and reset the board
    fun restartGame(rows: Int, cols: Int) {
        initializeGame(rows, cols)
    }

    // Helper function to get a list of valid moves (columns)
    fun getValidMoves(boardState: Array<IntArray>): List<Move> {
        val validMoves = mutableListOf<Move>()
        for (col in boardState[0].indices) {
            if (boardState[0][col] == 0) {  // Column is valid if the top row is empty
                validMoves.add(Move(col))
            }
        }
        return validMoves
    }

    // Helper function to apply a move and return the updated board state
    fun applyMove(boardState: Array<IntArray>, move: Move, player: Int): Array<IntArray> {
        val newBoardState = boardState.map { it.clone() }.toTypedArray()  // Create a copy of the board
        for (row in newBoardState.indices.reversed()) {
            if (newBoardState[row][move.column] == 0) {
                newBoardState[row][move.column] = player
                break
            }
        }
        return newBoardState
    }

    // Helper function to check if the game is over (either win or draw)
    fun isGameOver(boardState: Array<IntArray>): Boolean {
        val remainingMoves = boardState.sumOf { row -> row.count { it == 0 } }
        return remainingMoves == 0 ||
                checkWinCondition(boardState, 1, boardState.size, boardState[0].size) ||
                checkWinCondition(boardState, 2, boardState.size, boardState[0].size)
    }

    // Helper function to return the result of the game
    fun getResult(boardState: Array<IntArray>, player: Int): Int {
        return if (checkWinCondition(boardState, player, boardState.size, boardState[0].size)) {
            1  // The current player wins
        } else if (checkWinCondition(boardState, 3 - player, boardState.size, boardState[0].size)) {
            -1  // The opponent wins
        } else {
            0  // The game is a draw
        }
    }

    // Handles all win condition checks and returns a boolean
    private fun checkWinCondition(board: Array<IntArray>, player: Int, rows: Int, cols: Int): Boolean {
        // Horizontal Check
        for (row in 0 until rows) {
            for (col in 0 until cols - 3) {
                if (board[row][col] == player &&
                    board[row][col + 1] == player &&
                    board[row][col + 2] == player &&
                    board[row][col + 3] == player) {
                    return true
                }
            }
        }

        // Vertical Check
        for (col in 0 until cols) {
            for (row in 0 until rows - 3) {
                if (board[row][col] == player &&
                    board[row + 1][col] == player &&
                    board[row + 2][col] == player &&
                    board[row + 3][col] == player) {
                    return true
                }
            }
        }

        // Diagonal Check (Left to Right)
        for (row in 0 until rows - 3) {
            for (col in 0 until cols - 3) {
                if (board[row][col] == player &&
                    board[row + 1][col + 1] == player &&
                    board[row + 2][col + 2] == player &&
                    board[row + 3][col + 3] == player) {
                    return true
                }
            }
        }

        // Diagonal Check (Right to Left)
        for (row in 0 until rows - 3) {
            for (col in 3 until cols) {
                if (board[row][col] == player &&
                    board[row + 1][col - 1] == player &&
                    board[row + 2][col - 2] == player &&
                    board[row + 3][col - 3] == player) {
                    return true
                }
            }
        }

        return false
    }
}