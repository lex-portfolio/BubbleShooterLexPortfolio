package com.lex_portfolio.engine.game_elements.explosions;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.pools.UpdatablePool;
import com.lex_portfolio.engine.utils.Regions;

public class ExplosionPool extends UpdatablePool<Explosion> {

    private final TextureRegion[] regions;
    private final Sound sndExplosion;

    public ExplosionPool(TextureRegion region, int rows, int cols, int frames, Sound sndExplosion) {
        this.regions = Regions.split(region, rows, cols, frames);
        this.sndExplosion = sndExplosion;
    }

    @Override
    protected Explosion newObject() {
        return new Explosion(regions, sndExplosion);
    }
}
