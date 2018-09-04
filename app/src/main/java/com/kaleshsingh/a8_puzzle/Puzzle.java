package com.kaleshsingh.a8_puzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Puzzle {

    class Tile {
        int image;
        Bitmap blue;
        Bitmap green;

        Tile(int image, Bitmap blue, Bitmap green) {
            this.image = image;
            this.blue = blue;
            this.green = green;
        }
    }

    enum Move {
        ABOVE(-3), BELOW(3), LEFT(-1), RIGHT(1);

        private int move;

        Move(int move) {
            this.move = move;
        }

        public int getMove() {
            return move;
        }
    }

    private static final int BOARD_SIZE = 9;
    private static final int BLUE_TILES = 0;
    private static final int GREEN_TILES = 1;


    private final GridLayout board;
    private final Tile[] tiles;
    private final ImageView[] boardPositions;
    private final Bitmap[] solvedStateBlue;
    private final Bitmap[] solvedStateGreen;

    private int emptyTile;
    private Set<Integer> legalMoves;
    private boolean gameWon;

    static final String GAME_STATE = "game_state";
    static final String GAME_WON = "game_won";
    static final String EMPTY_TILE = "empty_tile";

    public int getEmptyTile() {
        return emptyTile;
    }

    public ImageView[] getBoardPositions() {
        return this.boardPositions;
    }

    public Tile[] getTiles() {
        return this.tiles;
    }

    public Set<Integer> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean getGameWon() {
        return this.gameWon;
    }

    Puzzle(GridLayout board, Bitmap[][] tileImages) {
        this.board = board;
        this.tiles = new Tile[BOARD_SIZE];
        initializeTiles(tileImages);
        this.boardPositions = initializeBoardPositions();
        solvedStateBlue = new Bitmap[9];
        solvedStateBlue[0] = null;
        System.arraycopy(tileImages[BLUE_TILES], 0, solvedStateBlue, 1, tileImages[BLUE_TILES].length);
        solvedStateGreen = new Bitmap[9];
        solvedStateGreen[0] = null;
        System.arraycopy(tileImages[GREEN_TILES], 0, solvedStateGreen, 1, tileImages[GREEN_TILES].length);
        this.gameWon = false;
        updateBoard();
    }

    public void updateBoard() {
        this.emptyTile = findEmptyTile();
        this.legalMoves = findLegalMoves();
        enableLegalMoves();
        gameWon = isGameWon();
        for (int i = 0; i < BOARD_SIZE; ++i) {
            boardPositions[i].setImageBitmap((gameWon) ? tiles[i].green : tiles[i].blue);
            if (gameWon) {
                boardPositions[i].setEnabled(false);
            }
        }
    }

    public int[] getGameState() {
        int[] gameState = new int[9];
        for (int i = 0; i < 9; ++i) {
            gameState[i] = tiles[i].image;
        }
        return gameState;
    }

    public void restoreGameState(boolean gameWon, int[] gameState, int emptyTile) {
        this.gameWon = gameWon;

        int pos = 0;
        for (int image : gameState) {
            tiles[pos].image = image;
            tiles[pos].blue = solvedStateBlue[image];
            tiles[pos].green = solvedStateGreen[image];
            ++pos;
        }

        for (int i = 0; i < BOARD_SIZE; ++i) {
            Log.d("INDEX",  "" + i);
            boardPositions[i].setImageBitmap((gameWon) ? tiles[i].green : tiles[i].blue);
            if (i == emptyTile) {
                boardPositions[i].setImageDrawable(null);
            }
        }

        updateBoard();
    }

    private ImageView[] initializeBoardPositions() {
        ImageView[] boardPositions = new ImageView[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; ++i) {
            boardPositions[i] = (ImageView) board.getChildAt(i);
        }
        return boardPositions;
    }

    private void initializeTiles(Bitmap[][] tileImages) {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            tiles[i] = (i == 0) ? new Tile(/* image= */i, /* blue= */null, /* green= */null)
                    : new Tile(/* image= */i, tileImages[BLUE_TILES][i - 1], tileImages[GREEN_TILES][i - 1]);
        }
    }

    private int findEmptyTile() {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            if (boardPositions[i].getDrawable() == null) {
                return i;
            }
        }
        return -1;
    }

    private Set<Integer> findLegalMoves() {
        Set<Integer> legalMoves = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; ++i) {
            int move = emptyTile - i;

            if (move == Move.LEFT.getMove()) {
                if (i % 3 != 0) {           // Slide tile left and emptyTile right
                    legalMoves.add(i);
                }
            } else if (move == Move.RIGHT.getMove()) {
                if (emptyTile % 3 != 0) {
                    legalMoves.add(i);       // Slide tile right and emptyTile left
                }
            } else if (move == Move.ABOVE.getMove() || move == Move.BELOW.getMove()) {
                legalMoves.add(i);          // Move vertically.
            }
        }
        return legalMoves;
    }

    private void enableLegalMoves() {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            if (legalMoves.contains(i)) {
                boardPositions[i].setEnabled(true);
            } else {
                boardPositions[i].setEnabled(false);
            }
        }
    }

    private boolean isGameWon() {
        List<Bitmap> gameState = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; ++i) {
            if (boardPositions[i].getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) boardPositions[i].getDrawable()).getBitmap();
                gameState.add(bitmap);
            } else {
                gameState.add(null);
            }
        }
        return gameState.equals(Arrays.asList(solvedStateBlue));
    }
}
