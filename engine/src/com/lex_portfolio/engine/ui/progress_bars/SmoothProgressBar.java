package com.lex_portfolio.engine.ui.progress_bars;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SmoothProgressBar extends ProgressBar {

    private static final float INC_SPEED = 0.7f;
    private static final float DEC_SPEED = 2f;

    private enum State {IDLE, INC, DEC}

    private State state = State.IDLE;
    private float visualProgress;

    public SmoothProgressBar(TextureRegion region, ProgressBar.Type type) {
        super(region, type);
    }

    public void setProgress(float progress) {
        if (progress < 0f || progress > 1f) {
            throw new IllegalArgumentException("progress: " + progress);
        }
        if (this.progress < progress) {
            state = State.INC;
        } else if (this.progress > progress) {
            state = State.DEC;
        } else {
            return;
        }
        this.progress = progress;
    }

    public void update(float deltaTime) {
        switch (state) {
            case IDLE:
                break;
            case INC:
                visualProgress += INC_SPEED * deltaTime;
                if (visualProgress >= progress) {
                    visualProgress = progress;
                    onProgressSynchronized();
                    state = State.IDLE;
                }
                break;
            case DEC:
                visualProgress -= DEC_SPEED * deltaTime;
                if (visualProgress <= progress) {
                    visualProgress = progress;
                    onProgressSynchronized();
                    state = State.IDLE;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    protected void onProgressSynchronized() {
    }

    @Override
    public void draw(SpriteBatch batch) {
        drawProgress(batch, visualProgress);
    }
}