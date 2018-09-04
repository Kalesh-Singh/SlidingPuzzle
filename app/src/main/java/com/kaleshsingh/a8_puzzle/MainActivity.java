package com.kaleshsingh.a8_puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_LEGAL_RANDOM_MOVES = 200;
    private static final int MAX_LEGAL_RANDOM_MOVES = 300;

    private Puzzle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        puzzle = new Puzzle(findViewById(R.id.board_grid), getTileImages());
        shuffleBoard();
        setClickListeners();

        if (savedInstanceState != null) {
            puzzle.restoreGameState(
                    savedInstanceState.getBoolean(Puzzle.GAME_WON),
                    savedInstanceState.getIntArray(Puzzle.GAME_STATE),
                    savedInstanceState.getInt(Puzzle.EMPTY_TILE));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Puzzle.GAME_STATE, puzzle.getGameState());
        outState.putBoolean(Puzzle.GAME_WON, puzzle.getGameWon());
        outState.putInt(Puzzle.EMPTY_TILE, puzzle.getEmptyTile());
    }

    private void shuffleBoard() {
        for (int i = 0;
             i < (Math.random() * (MAX_LEGAL_RANDOM_MOVES - MIN_LEGAL_RANDOM_MOVES))
                     + MIN_LEGAL_RANDOM_MOVES;
             ++i) {
            moveRandomTile();
        }
        if (puzzle.getGameWon()) {
            shuffleBoard();
        }
    }

    private void moveRandomTile() {
        List<Integer> legalMoves = new ArrayList<>(puzzle.getLegalMoves());
        int move = legalMoves.get((int) (Math.random() * legalMoves.size()));
        swapTiles(move);
    }

    private void onTileClick(View v) {
        final ImageView clickedView = (ImageView) v;
        int clickedTile = Integer.parseInt((String) clickedView.getTag());
        swapTiles(clickedTile);

        if (puzzle.getGameWon()) {
            Toast.makeText(this, R.string.winning_message, Toast.LENGTH_LONG).show();
        }

    }

    private void swapTiles(int clickedTile) {
        int emptyTile = puzzle.getEmptyTile();
        ImageView[] boardPositions = puzzle.getBoardPositions();
        Drawable clickedDrawable = boardPositions[clickedTile].getDrawable();
        boardPositions[emptyTile].setImageDrawable(clickedDrawable);
        boardPositions[clickedTile].setImageDrawable(null);

        Puzzle.Tile[] tiles = puzzle.getTiles();
        Puzzle.Tile temp = tiles[emptyTile];
        tiles[emptyTile] = tiles[clickedTile];
        tiles[clickedTile] = temp;

        puzzle.updateBoard();
    }

    private void setClickListeners() {
        for (ImageView imageView : puzzle.getBoardPositions()) {
            imageView.setOnClickListener(v -> onTileClick(v));
        }
    }


    private Bitmap[][] getTileImages() {
        Bitmap[][] tileImages = new Bitmap[2][8];

        Bitmap spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.number_sprites);
        int tile_width = spriteSheet.getWidth() / 10;
        int tileHeight = spriteSheet.getHeight() / 3;

        for (int i = 0; i < 3; i += 2) {
            for (int j = 1; j < 9; ++j) {
                tileImages[(i == 0) ? i : i - 1][j - 1]
                        = Bitmap
                        .createBitmap(spriteSheet, j * tile_width, i * tileHeight,
                                tile_width, tileHeight);
            }
        }
        return tileImages;
    }
}
