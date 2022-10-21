package com.lex_portfolio.bubble_shooter.balls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.pools.UpdatablePool;

public class DebugMarkersPool extends UpdatablePool<DebugMarker> {

    private final TextureRegion markerRegion;

    DebugMarkersPool(TextureRegion markerRegion) {
        this.markerRegion = markerRegion;
    }

    @Override
    protected DebugMarker newObject() {
        return new DebugMarker(markerRegion);
    }
}
