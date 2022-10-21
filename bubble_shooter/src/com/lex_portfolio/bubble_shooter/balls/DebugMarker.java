package com.lex_portfolio.bubble_shooter.balls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

class DebugMarker extends Sprite {

    private float lifeTime;
    private float timer;

    DebugMarker(TextureRegion region) {
        super(region);
    }

    void start(Vector2 pos, float height, float angle, float lifeTime) {
        this.pos.set(pos);
        this.angle = angle;
        this.lifeTime = lifeTime;
        setHeightProportion(height);
        timer = 0f;
    }

    @Override
    public void update(float deltaTime) {
        timer += deltaTime;
        if (timer >= lifeTime) destroy();
    }
}
