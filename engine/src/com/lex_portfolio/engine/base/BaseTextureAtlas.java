package com.lex_portfolio.engine.base;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.exceptions.RegionNotFoundException;

public class BaseTextureAtlas extends TextureAtlas {

    public BaseTextureAtlas(FileHandle packFile) {
        super(packFile);
    }

    public TextureRegion getRegion(String name) {
        TextureRegion region = findRegion(name);
        if(region == null) throw new RegionNotFoundException(name);
        return region;
    }
}
