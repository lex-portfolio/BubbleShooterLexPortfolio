package com.lex_portfolio.bubble_shooter.balls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.game_elements.explosions.ExplosionPool;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.pools.DestroyablePool;

public class PoolBalls extends DestroyablePool<Ball> {

    private final ExplosionPool effectBallExplosionPool;
    private final DebugMarkersPool debugMarkerBallDeletedPool;
    private final DebugMarkersPool debugMarkersBallDownfallPool;

    private final TextureRegion[] regions;
    private float ballSize;

    public PoolBalls(
            BaseTextureAtlas atlas
    ) {
        this.effectBallExplosionPool = new ExplosionPool(atlas.getRegion("explosion_1"), 4, 4, 16, null);
        this.debugMarkerBallDeletedPool = new DebugMarkersPool(atlas.getRegion("cross_1"));
        this.debugMarkersBallDownfallPool = new DebugMarkersPool(atlas.getRegion("arrow_1"));

        Ball.Type[] types = Ball.Type.values();
        regions = new TextureRegion[types.length];
        for (int i = 0; i < types.length; i++) {
            regions[i] = atlas.getRegion(("ball_" + types[i]).toLowerCase());
        }
    }

    public void setBallSize(float ballSize) {
        this.ballSize = ballSize;
    }

    public Ball obtain(Ball.Type type) {
        Ball ball = super.obtain();
        ball.setSize(ballSize);
        ball.setType(type);
        return ball;
    }

    public void updateAndFreeAllDestroyed(float deltaTime, Rect gridBounds) {
        for (int i = 0, cnt = activeObjects.size(); i < cnt; i++)
            activeObjects.get(i).update(deltaTime, gridBounds);
        freeAllDestroyedActiveObjects();
    }

    @Override
    protected Ball newObject() {
        return new Ball(regions, effectBallExplosionPool, debugMarkerBallDeletedPool, debugMarkersBallDownfallPool);
    }

    public TextureRegion[] getRegions() {
        return regions;
    }

    public DebugMarkersPool getDebugMarkerBallDeletedPool() {
        return debugMarkerBallDeletedPool;
    }

    public DebugMarkersPool getDebugMarkersBallDownfallPool() {
        return debugMarkersBallDownfallPool;
    }

    public ExplosionPool getEffectBallExplosionPool() {
        return effectBallExplosionPool;
    }

    @Override
    public void dispose() {
        debugMarkerBallDeletedPool.dispose();
        debugMarkersBallDownfallPool.dispose();
        effectBallExplosionPool.dispose();
        super.dispose();
    }
}
