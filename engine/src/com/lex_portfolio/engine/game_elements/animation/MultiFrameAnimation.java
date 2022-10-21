package com.lex_portfolio.engine.game_elements.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

public class MultiFrameAnimation extends Sprite {

    private float animateInterval;
    private float animateTimer;
    private boolean isLoop;
    private boolean active;

    @SuppressWarnings("unused")
    public MultiFrameAnimation(TextureRegion region, int rows, int cols) {
        super(region, rows, cols);
    }

    public void start(Vector2 pos, float height, float lifeTime, boolean isLoop) {
        this.pos.set(pos);
        this.isLoop = isLoop;
        setHeightProportion(height);
        frame = 0;
        animateTimer = 0f;
        animateInterval = lifeTime / regions.length;
        active = true;
    }

    public void stop() {
        active = false;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;
        animateTimer += deltaTime;
        if (animateTimer >= animateInterval) {
            animateTimer = 0f;
            if (++frame == regions.length) {
                frame = 0;
                if (!isLoop) {
                    active = false;
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;
        super.draw(batch);
    }
}
