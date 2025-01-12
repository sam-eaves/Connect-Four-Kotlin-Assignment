package com.example.connectfour.model

import com.example.connectfour.viewmodel.GameViewModel

data class Move(val column: Int)

// Represents a node in the MCTS tree
data class MCTSNode(
    val boardState: Array<IntArray>,
    val currentPlayer: Int,
    var parent: MCTSNode? = null,
    val children: MutableList<MCTSNode> = mutableListOf(),
    var visitCount: Int = 0,
    var winScore: Double = 0.0,
    var lastMove: Move? = null
) {
    fun isLeaf(): Boolean = children.isEmpty()
}

// The MCTS algorithm logic, now cleaned up
class MCTS_AI(private val gameState: GameViewModel.GameState, private val gameViewModel: GameViewModel) {

    // Main function to find the best move using MCTS
    fun findBestMove(): Move? {
        val rootNode = MCTSNode(gameState.board, gameState.currentPlayer)
        val mctsAlgorithm = MCTSAlgorithm(rootNode, gameViewModel)
        mctsAlgorithm.runSearch(10000)  // Run MCTS with a set number of iterations

        // Get the best move from the search
        return rootNode.children.maxByOrNull { it.visitCount }?.lastMove
    }
}

// The MCTS algorithm itself
class MCTSAlgorithm(private val root: MCTSNode, private val gameViewModel: GameViewModel) {

    // Run the MCTS algorithm for a specified number of iterations
    fun runSearch(iterations: Int) {
        repeat(iterations) {
            val selectedNode = selection(root)
            val expandedNode = expansion(selectedNode)
            val result = simulation(expandedNode)
            backpropagation(expandedNode, result)
        }
    }

    // Selection phase of MCTS
    private fun selection(node: MCTSNode): MCTSNode {
        var current = node
        while (!current.isLeaf() && isFullyExpanded(current)) {
            current = current.children.maxByOrNull { ucb1(it) }!!
        }
        return current
    }

    // Expansion phase of MCTS
    private fun expansion(node: MCTSNode): MCTSNode {
        if (!isFullyExpanded(node)) {
            val untriedMoves = gameViewModel.getValidMoves(node.boardState).filter { move ->
                node.children.none { it.lastMove == move }
            }
            val newMove = untriedMoves.random()
            val newBoardState = gameViewModel.applyMove(node.boardState, newMove, node.currentPlayer)
            val newNode = MCTSNode(boardState = newBoardState, currentPlayer = 3 - node.currentPlayer, parent = node, lastMove = newMove)
            node.children.add(newNode)
            return newNode
        }
        return node
    }

    // Simulation phase of MCTS
    private fun simulation(node: MCTSNode): Int {
        var currentState = node.boardState
        var currentPlayer = node.currentPlayer

        // Continue simulating until the game is over (win, draw)
        while (!gameViewModel.isGameOver(currentState)) {
            val validMoves = gameViewModel.getValidMoves(currentState)

            // Ensure there are valid moves left
            if (validMoves.isEmpty()) {
                break // No more moves, the game is over
            }

            // Pick a random move and apply it
            val randomMove = validMoves.random()
            currentState = gameViewModel.applyMove(currentState, randomMove, currentPlayer)
            currentPlayer = 3 - currentPlayer // Switch player
        }

        // Return the result of the game for the current player
        return gameViewModel.getResult(currentState, node.currentPlayer)
    }

    // Backpropagation phase of MCTS
    private fun backpropagation(node: MCTSNode, result: Int) {
        var current: MCTSNode? = node
        while (current != null) {
            current.visitCount++
            current.winScore += result
            current = current.parent
        }
    }

    // Upper Confidence Bound for Trees (UCB1) to balance exploration and exploitation
    private fun ucb1(node: MCTSNode): Double {
        if (node.visitCount == 0) return Double.MAX_VALUE
        return node.winScore / node.visitCount + kotlin.math.sqrt(2.0 * kotlin.math.ln(node.parent!!.visitCount.toDouble()) / node.visitCount)
    }

    // Check if a node is fully expanded by comparing the number of children to the number of valid moves
    private fun isFullyExpanded(node: MCTSNode): Boolean {
        return node.children.size == gameViewModel.getValidMoves(node.boardState).size
    }
}
