package com.lex_portfolio.engine.game_elements.spinner;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

public class Spinner extends Sprite {

    public enum State {IDLE, SHORT_ROTATE, LONG_ROTATE}

    private static final float ROTATE_FREQ = 3f;
    private static final float W_SHORT_SWAPPING = -120f * ROTATE_FREQ;
    private static final float W_LONG_SWAPPING = -(120f + 360f) * ROTATE_FREQ;

    private final SpinnerListener listener;
    private final Vector2[] positions = new Vector2[3];
    private final Vector2 rotateCenter = new Vector2();
    private final Vector2 rad = new Vector2();
    private final float radABS;
    private int upperIndex;
    private float w;
    private float targetAngle;
    private State state;

    public Spinner(
            TextureRegion region,
            Vector2 pos,
            Vector2 eccentricityPercent,
            float height,
            float radPercent,
            SpinnerListener listener
    ) {
        super(region);
        this.listener = listener;
        for (int i = 0; i < positions.length; i++) positions[i] = new Vector2();
        this.pos.set(pos);
        setHeightProportion(height);
        this.radABS = halfHeight * radPercent;
        rotateCenter.set(halfWidth * eccentricityPercent.x, halfHeight * eccentricityPercent.y);
        updatePositions();
    }

    private void updatePositions() {
        for (int i = 0; i < positions.length; i++) {
            rad.set(0f, radABS).rotateDeg(angle + i * 120);
            positions[i].set(getLeft(), getBottom()).add(rotateCenter).add(rad);
        }
        listener.onSpinnerChangePosition(this);
    }

    public void startShortRotate() {
        state = State.SHORT_ROTATE;
        upperIndex++;
        targetAngle = -upperIndex * 120f;
        if (upperIndex == positions.length) upperIndex = 0;
        w = W_SHORT_SWAPPING;
    }

    public void starRotateLong() {
        state = State.LONG_ROTATE;
        upperIndex++;
        targetAngle = -(upperIndex * 120f + 360f);
        if (upperIndex == positions.length) upperIndex = 0;
        w = W_LONG_SWAPPING;
    }

    @Override
    public void update(float deltaTime) {
        switch (state) {
            case IDLE:
                break;
            case SHORT_ROTATE:
            case LONG_ROTATE:
                angle += w * deltaTime;
                if (angle <= targetAngle) {
                    setIdle();
                    listener.onSpinnerCompleteRotation(this);
                } else {
                    updatePositions();
                }
                break;
            default:
                throw new RuntimeException("state = " + state);
        }
    }

    public void setIdle(int upperIndex) {
        this.upperIndex = upperIndex;
        setIdle();
    }

    private void setIdle() {
        state = State.IDLE;
        angle = -120f * upperIndex;
        updatePositions();
    }

    public void setShortRotate(int upperIndex, float angle) {
        this.upperIndex = upperIndex;
        state = State.SHORT_ROTATE;
        this.angle = angle;
        w = W_SHORT_SWAPPING;
        targetAngle = -(getPrevIndex() + 1) * 120f;
    }

    public void setLongRotate(int upperIndex, float angle) {
        this.upperIndex = upperIndex;
        state = State.LONG_ROTATE;
        this.angle = angle;
        w = W_LONG_SWAPPING;
        targetAngle = -((getPrevIndex() + 1) * 120f + 360f);
    }

    public int getUpperIndex() {
        return upperIndex;
    }

    public Vector2 getPosition(int i) {
        return positions[i];
    }

    public int getPrevIndex() {
        int prevIndex = upperIndex - 1;
        if (prevIndex == -1) prevIndex = positions.length - 1;
        return prevIndex;
    }

    public State getState() {
        return state;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(
                regions[frame],
                getLeft(), getBottom(),
                rotateCenter.x, rotateCenter.y,
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                angle
        );
    }
}
