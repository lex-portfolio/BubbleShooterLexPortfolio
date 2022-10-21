package com.lex_portfolio.engine.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.pools.Destroyable;
import com.lex_portfolio.engine.pools.Poolable;
import com.lex_portfolio.engine.pools.Updatable;
import com.lex_portfolio.engine.utils.Regions;
import com.lex_portfolio.engine.view.Drawable;

public class Sprite extends Rect implements Poolable, Destroyable, Updatable, Drawable {

    protected final TextureRegion[] regions;
    private float scaleX = 1f;
    private float scaleY = 1f;
    protected float angle = 0f;
    protected int frame;

    protected boolean isDestroyed;

    public Sprite(TextureRegion region) {
        if (region == null) throw new RuntimeException("Create Sprite with null region");
        regions = new TextureRegion[1];
        regions[0] = region;
    }

    public Sprite(TextureRegion[] regions) {
        if (regions == null) throw new NullPointerException();
        this.regions = regions;
    }

    public Sprite(TextureRegion region, int rows, int cols) {
        regions = Regions.split(region, rows, cols);
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isDestroyed) throw new RuntimeException();
        batch.draw(
                regions[frame],
                getLeft(), getBottom(),
                halfWidth, halfHeight,
                getWidth(), getHeight(),
                scaleX, scaleY, angle
        );
    }

    public void setWidthProportion(float width) {
        float aspect = regions[frame].getRegionWidth() / (float) regions[frame].getRegionHeight();
        setWidthProportion(width, aspect);
    }

    public void setHeightProportion(float height) {
        float aspect = regions[frame].getRegionWidth() / (float) regions[frame].getRegionHeight();
        setHeightProportion(height, aspect);
    }

    @SuppressWarnings("unused")
    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setScales(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void addScale(float n) {
        scaleX += n;
        scaleY += n;
    }

    public float getAngle() {
        return angle;
    }

    @SuppressWarnings("unused")
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    @SuppressWarnings("unused")
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScales() {
        if (scaleX != scaleY) throw new IllegalStateException();
        return scaleX;
    }

    public void destroy() {
        isDestroyed = true;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void onObtainFromPool() {
        isDestroyed = false;
    }

    public void setPos(Vector2 pos) {
        this.pos.set(pos);
    }

    public void setPos(float x, float y) {
        pos.set(x, y);
    }

    @Override
    public void onReturnToPool() {
    }

    @Override
    public String toString() {
        return "Sprite: " + " angle = " + angle + " scaleX = " + scaleX + " scaleY = " + scaleY + " " + super.toString();
    }
}