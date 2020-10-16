package edu.iastate.cs472.proj1;

import java.util.Arrays;

public class AlphaBetaSearch {
    private static int BOARD_SIZE = 8;
    private static int MAX_DEPTH = 6;

    private CheckersData board;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    public void setCheckersData(CheckersData board) {
        this.board = board;
    }

    // Todo: You can implement your helper methods here

    /**
     * You need to implement the Alpha-Beta pruning algorithm here to
     * find the best move at current stage.
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        double minValue = Double.POSITIVE_INFINITY;
        CheckersMove chosenMove = null;
        for (CheckersMove move : legalMoves) {
            CheckersData clone = new CheckersData(board);
            clone.makeMove(move);
            double value = maxValue(clone, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
            if (value < minValue) {
                minValue = value;
                chosenMove = move;
            }
        }

        return chosenMove;
    }

    private double maxValue(CheckersData board, double alpha, double beta, int depth) {}

    /**
     * Find the value to proceed with at a min node, meaning it is BLACK's turn.
     * Uses alpha-beta pruning.
     *
     * @param board
     * @param alpha
     * @param beta
     * @param depth
     * @return the min value at the node
     */
    private double minValue(CheckersData board, double alpha, double beta, int depth) {
        if (depth == MAX_DEPTH)
            return evaluate(board);
        double value = Double.POSITIVE_INFINITY;
        for (CheckersMove move : board.getLegalMoves(CheckersData.BLACK)) {
            // make a clone with the move made
            CheckersData clone = new CheckersData(board);
            boolean isKingJump = clone.makeMove(move);

            // it's possible we have another legal move after jumping
            if (!isKingJump && move.isJump()) {
                CheckersMove[] legalJumps = clone.getLegalJumpsFrom(CheckersData.BLACK, move.toRow, move.toCol);
                if (legalJumps != null) { // we have more jumps
                    // TODO: fix this
                    value = Math.min(value, minValue(
                            clone, alpha, beta, depth + 1));

                }
            }

            // time to go deeper
            value = Math.min(value, maxValue(
                    clone, alpha, beta, depth + 1));

            // say goodbye to dates and hello to pruning
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    /**
     * Evaluation function for a board state that works as follows:
     *
     * Each normal piece is worth 1 point - however, if it is 2 rows away from promotion it is worth 1.2 points,
     * and if it is 1 row away it is worth 1.45 points. Pieces on a side edge are worth 75% of their normal
     * value because they can only move away from the wall, so they only have half of their normal moves.
     *
     * Each kinged piece is worth 2.5 points, but the value is cut by 1/4 for every wall it is touching.
     *
     * Red pieces get positive values, black pieces get negative values. If there are no black pieces left, red gets
     * a score of 100, and vice-versa: if there are no reds left, black gets -100.
     *
     * @param board
     * @return
     */
    private double evaluate(CheckersData board) {
        double red = 0;
        double black = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i % 2 == j % 2) { // checkers are only on every other square
                    int piece = board.pieceAt(i, j);
                    if (piece == CheckersData.RED) {
                        double value = 1;
                        if (i == 1) {
                            value = 1.45;
                        } else if (i == 2) {
                            value = 1.2;
                        }
                        if (j == 0 || j == BOARD_SIZE - 1) {
                            value *= 0.75;
                        }
                        red += value;
                    } else if (piece == CheckersData.BLACK) {
                        double value = 1;
                        if (i == BOARD_SIZE - 2) {
                            value = 1.45;
                        } else if (i == BOARD_SIZE - 3) {
                            value = 1.2;
                        }
                        if (j == 0 || j == BOARD_SIZE - 1) {
                            value *= 0.75;
                        }
                        black += value;
                    } else if (piece == CheckersData.RED_KING || piece == CheckersData.BLACK_KING) {
                        double value = 2.5;
                        if (i == 0 || i == BOARD_SIZE - 1) {
                            value *= 0.75;
                        }
                        if (j == 0 || j == BOARD_SIZE - 1) {
                            value *= 0.75;
                        }
                        if (piece == CheckersData.RED_KING) {
                            red += value;
                        } else {
                            black += value;
                        }
                    }
                }
            }
        }

        if (red == 0) return -100;
        if (black == 0) return 100;
        return red - black;
    }
}
