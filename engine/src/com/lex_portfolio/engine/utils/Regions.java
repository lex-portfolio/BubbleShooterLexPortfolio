package com.lex_portfolio.engine.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Regions {

    public static TextureRegion[] split(TextureRegion region, int rows, int cols) {
        return split(region, rows, cols, rows * cols);
    }

    public static TextureRegion[] split(TextureRegion region, int rows, int cols, int framesCount) {
        if(region == null) throw new NullPointerException("Split null region");
        TextureRegion[] regions = new TextureRegion[framesCount];
        int tileWidth = region.getRegionWidth() / cols;
        int tileHeight = region.getRegionHeight() / rows;

        int frame = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                regions[frame] = new TextureRegion(region, tileWidth * j, tileHeight * i, tileWidth, tileHeight);
                if(frame == framesCount - 1) return regions;
                frame++;
            }
        }
        throw new RuntimeException("rows: " + rows + " cols: " + cols + " frameCount: " + framesCount + " frame: " + frame);
    }
}
