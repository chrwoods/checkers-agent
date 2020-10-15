package edu.iastate.cs472.proj1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author cswoods
 *
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        // note: since it's an array of primitives, everything is by default initialized to 0 = EMPTY.
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i % 2 == j % 2) { // we need to place a piece
                    if (i < 3) { // we should place a black piece
                        board[i][j] = BLACK;
                    } else if (i > board.length - 3) { // we should place a red piece
                        board[i][j] = RED;
                    }
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     * @return  true if the piece becomes a king, otherwise false
     */
    boolean makeMove(CheckersMove move) {
        return makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol, move.isJump());
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     * @return        true if the piece becomes a king, otherwise false
     */
    private boolean makeMove(int fromRow, int fromCol, int toRow, int toCol, boolean isJump) {
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        board[toRow][toCol] = pieceAt(fromRow, fromCol);
        board[fromRow][fromCol] = EMPTY;
        // 2. if this move is a jump, remove the captured piece
        if (isJump) {
            board[(fromRow + toRow) / 2][(fromCol + toCol) / 2] = EMPTY;
        }
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
        if (toRow == 0 && pieceAt(toRow, toCol) == RED) { // make a red king if it reached the top
            board[toRow][toCol] = RED_KING;
        } else if (toRow == board.length - 1 && pieceAt(toRow, toCol) == BLACK) { // make a black king if it reached the bottom
            board[toRow][toCol] = BLACK_KING;
        } else { // no king here
            return false;
        }
        return true;
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        List<CheckersMove> legalMoves = new ArrayList<>();

        // try to find any jumps
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i % 2 == j % 2) { // checkers are only on every other square
                    CheckersMove[] jumps = getLegalJumpsFrom(player, i, j);
                    if (jumps != null)
                        legalMoves.addAll(Arrays.asList(jumps));
                }
            }
        }
        // if we have jumps, return them
        if (!legalMoves.isEmpty())
            return legalMoves.toArray(new CheckersMove[0]);

        // try to find any legal non-jumps (walks)
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i % 2 == j % 2) { // checkers are only on every other square
                    legalMoves.addAll(getLegalWalksFrom(player, i, j));
                }
            }
        }
        // if we have walks, return them
        if (!legalMoves.isEmpty())
            return legalMoves.toArray(new CheckersMove[0]);

        return null;
    }

    /**
     * Added helper method.
     *
     * Return a list of the legal non-jumps (walks) that the specified
     * player can make starting from the specified row and column.
     * If no such "walks" are possible, an empty list is returned.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    private List<CheckersMove> getLegalWalksFrom(int player, int row, int col) {
        int piece = pieceAt(row, col);
        boolean canWalkDown = false; // if the given piece has the ability to walk down
        boolean canWalkUp = false; // if the given piece has the ability to walk up

        if (player == RED) {
            if (piece == RED) {
                canWalkUp = true;
            } else if (piece == RED_KING) {
                canWalkUp = true;
                canWalkDown = true;
            } else {
                return Collections.emptyList();
            }
        } else { // black player
            if (piece == BLACK) {
                canWalkDown = true;
            } else if (piece == BLACK_KING) {
                canWalkUp = true;
                canWalkDown = true;
            } else {
                return Collections.emptyList();
            }
        }

        List<CheckersMove> legalWalks = new ArrayList<>(4); // we can find max 4 walks for each spot

        if (canWalkDown) {
            if (row + 1 < board.length) { // check if we can walk down
                if (col + 1 < board[0].length) { // check if we can walk right
                    if (pieceAt(row + 1, col + 1) == EMPTY) { // check if we have space to move there
                        legalWalks.add(new CheckersMove(row, col, row + 1, col + 1));
                    }
                }
                if (col - 1 >= 0) { // check if we can walk left
                    if (pieceAt(row + 1, col - 1) == EMPTY) { // check if we have space to move there
                        legalWalks.add(new CheckersMove(row, col, row + 1, col - 1));
                    }
                }
            }
        }

        if (canWalkUp) {
            if (row - 1 >= 0) { // check if we can walk up
                if (col + 1 < board[0].length) { // check if we can walk right
                    if (pieceAt(row - 1, col + 1) == EMPTY) { // check if we have space to move there
                        legalWalks.add(new CheckersMove(row, col, row - 1, col + 1));
                    }
                }
                if (col - 1 >= 0) { // check if we can walk left
                    if (pieceAt(row - 1, col - 1) == EMPTY) { // check if we have space to move there
                        legalWalks.add(new CheckersMove(row, col, row - 1, col - 1));
                    }
                }
            }
        }

        return legalWalks;
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalWalksFrom() method.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        int piece = pieceAt(row, col);
        boolean canJumpDown = false; // if the given piece has the ability to jump down
        boolean canJumpUp = false; // if the given piece has the ability to jump up

        if (player == RED) {
            if (piece == RED) {
                canJumpUp = true;
            } else if (piece == RED_KING) {
                canJumpUp = true;
                canJumpDown = true;
            } else {
                return null;
            }
        } else { // black player
            if (piece == BLACK) {
                canJumpDown = true;
            } else if (piece == BLACK_KING) {
                canJumpUp = true;
                canJumpDown = true;
            } else {
                return null;
            }
        }

        List<CheckersMove> legalJumps = new ArrayList<>(4); // we can find max 4 jumps for each spot

        if (canJumpDown) {
            if (row + 2 < board.length) { // check if we can jump downwards
                if (col + 2 < board[0].length) { // check if we can jump right
                    if (pieceAt(row + 2, col + 2) == EMPTY) { // check if we have space to move there
                        if (canTakeAs(pieceAt(row + 1, col + 1), player)) { // check if we have something to jump over that we can take
                            legalJumps.add(new CheckersMove(row, col, row + 2, col + 2));
                        }
                    }
                }
                if (col - 2 >= 0) { // check if we can jump left
                    if (pieceAt(row + 2, col - 2) == EMPTY) { // check if we have space to move there
                        if (canTakeAs(pieceAt(row + 1, col - 1), player)) { // check if we have something to jump over that we can take
                            legalJumps.add(new CheckersMove(row, col, row + 2, col - 2));
                        }
                    }
                }
            }
        }

        if (canJumpUp) {
            if (row - 2 >= 0) { // check if we can jump upwards
                if (col + 2 < board[0].length) { // check if we can jump right
                    if (pieceAt(row - 2, col + 2) == EMPTY) { // check if we have space to move there
                        if (canTakeAs(pieceAt(row - 1, col + 1), player)) { // check if we have something to jump over that we can take
                            legalJumps.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }
                }
                if (col - 2 >= 0) { // check if we can jump left
                    if (pieceAt(row - 2, col - 2) == EMPTY) { // check if we have space to move there
                        if (canTakeAs(pieceAt(row - 1, col - 1), player)) { // check if we have something to jump over that we can take
                            legalJumps.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }
                }
            }
        }

        if (legalJumps.isEmpty()) return null;
        return legalJumps.toArray(new CheckersMove[0]);
    }

    /**
     * Helper method to determine if the given player is allowed
     * to take the given target piece, returning true if it can and false otherwise.
     *
     * @param piece
     * @param player
     * @return
     */
    private boolean canTakeAs(int piece, int player) {
        if (player == RED)
            if (piece == BLACK || piece == BLACK_KING)
                return true;
        else if (player == BLACK)
            if (piece == RED || piece == RED_KING)
                return true;
        return false;
    }

}