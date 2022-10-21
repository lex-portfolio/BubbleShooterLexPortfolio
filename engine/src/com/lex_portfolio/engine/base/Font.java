package com.lex_portfolio.engine.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Font extends BitmapFont {

    public Font(String fontFile, String imageFile) {
        super(Gdx.files.internal(fontFile), Gdx.files.internal(imageFile), false, false);
        getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int error = Gdx.gl.glGetError();
        if (error != Gdx.gl.GL_NO_ERROR) throw new RuntimeException("GL_ERROR = " + error);
    }

    public void setWorldSize(float worldSize) {
        BitmapFontData data = getData();
        data.setScale(1f);
        data.setScale(worldSize / getCapHeight());
    }

    @SuppressWarnings({"UnusedReturnValue", "SpellCheckingInspection"})
    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, int halign) {
        return draw(batch, str, x, y, 0f, halign, false);
    }
}
