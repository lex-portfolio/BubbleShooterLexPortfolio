package com.lex_portfolio.engine.ui.progress_bars;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.sprites.Sprite;

public class ProgressBar extends Sprite {

    public enum Type {HORIZONTAL, VERTICAL}

    private final Type type;
    protected float progress;

    @SuppressWarnings("unused")
    public ProgressBar(TextureRegion region, Type type) {
        super(region);
        this.type = type;
    }

    public float getProgress() {
        return progress;
    }

    @Override
    public void draw(SpriteBatch batch) {
        drawProgress(batch, progress);
    }

    void drawProgress(SpriteBatch batch, float progress) {
        TextureRegion region = regions[frame];
        switch (type) {
            case HORIZONTAL:
                batch.draw(
                        region.getTexture(),
                        getLeft(), getBottom(),
                        halfWidth, halfHeight,
                        getWidth() * progress, getHeight(),
                        getScaleX(), getScaleY(),
                        angle,
                        region.getRegionX(), region.getRegionY(),
                        Math.round(region.getRegionWidth() * progress), region.getRegionHeight(),
                        false, false
                );
                break;
            case VERTICAL:
                int drawRegionHeight = Math.round(region.getRegionHeight() * progress);
                batch.draw(
                        region.getTexture(),
                        getLeft(), getBottom(),
                        halfWidth, halfHeight,
                        getWidth(), getHeight() * progress,
                        getScaleX(), getScaleY(),
                        angle,
                        region.getRegionX(), region.getRegionY() + (region.getRegionHeight() - drawRegionHeight),
                        region.getRegionWidth(), drawRegionHeight,
                        false, false
                );
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}