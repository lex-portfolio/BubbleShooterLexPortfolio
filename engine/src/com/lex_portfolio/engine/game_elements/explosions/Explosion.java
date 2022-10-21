package com.lex_portfolio.engine.game_elements.explosions;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

public class Explosion extends Sprite {

    private final Sound sound;
    private float animateInterval;
    private float animateTimer;

    Explosion(TextureRegion[] regions, Sound sound) {
        super(regions);
        this.sound = sound;
    }

    public void start(Vector2 pos, float height, float lifeTime, float volume) {
        this.pos.set(pos);
        frame = 0;
        setHeightProportion(height);
        animateTimer = 0f;
        animateInterval = lifeTime / regions.length;
        if (sound != null) sound.play(volume);
    }

    @Override
    public void update(float deltaTime) {
        animateTimer += deltaTime;
        if (animateTimer >= animateInterval) {
            animateTimer = 0f;
            if (++frame == regions.length) destroy();
        }
    }
}
