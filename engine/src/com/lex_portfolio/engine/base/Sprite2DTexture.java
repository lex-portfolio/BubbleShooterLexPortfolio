package com.lex_portfolio.engine.base;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Sprite2DTexture extends Texture {

    public Sprite2DTexture(FileHandle file) {
        super(file, true);
        setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
    }
}