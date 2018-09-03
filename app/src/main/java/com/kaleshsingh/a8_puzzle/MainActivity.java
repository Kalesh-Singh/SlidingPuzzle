package com.kaleshsingh.a8_puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private Puzzle puzzle;
    private static int TILE_WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        puzzle = new Puzzle((GridLayout) findViewById(R.id.board_grid), getTileImages());
        setClickListeners();
    }

    private void onTileClick(View v) {
        final ImageView clickedView = (ImageView) v;
        int move = puzzle.getEmptyTile() - Integer.parseInt((String) clickedView.getTag());
        swapTiles(clickedView);
    }

    private void swapTiles(ImageView clickedView) {
        int emptyTile = puzzle.getEmptyTile();
        int clickedTile = Integer.parseInt((String) clickedView.getTag());
        ImageView[] boardPositions = puzzle.getBoardPositions();
        boardPositions[emptyTile].setImageBitmap(((BitmapDrawable) clickedView.getDrawable()).getBitmap());
        boardPositions[clickedTile].setImageBitmap(null);
        boardPositions[clickedTile].setImageDrawable(null);

        Puzzle.Tile[] tiles = puzzle.getTiles();
        Puzzle.Tile temp = tiles[emptyTile];
        tiles[emptyTile] = tiles[clickedTile];
        tiles[clickedTile] = temp;

        puzzle.updateBoard();
    }

    private void setClickListeners() {
        for (ImageView imageView : puzzle.getBoardPositions()) {
//            imageView.setClickable(true);
            imageView.setOnClickListener(v -> onTileClick(v));
        }
    }


    private Bitmap[][] getTileImages() {
        Bitmap[][] tileImages = new Bitmap[2][8];

        Bitmap spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.number_sprites);
        TILE_WIDTH = spriteSheet.getWidth() / 10;
        int tileHeight = spriteSheet.getHeight() / 3;

        for (int i = 0; i < 3; i += 2) {
            for (int j = 1; j < 9; ++j) {
                tileImages[(i == 0) ? i : i-1][j-1] = Bitmap
                        .createBitmap(
                                spriteSheet,
                                j * TILE_WIDTH,
                                i * tileHeight,
                                TILE_WIDTH,
                                tileHeight);
            }
        }
        return tileImages;
    }
}
