package com.lex_portfolio.bubble_shooter.buttons;

import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.BONUS_READY;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.sounds.Sounds;
import com.lex_portfolio.engine.game_elements.animation.MultiFrameAnimation;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.progress_bars.SmoothProgressBar;

public class BonusButton extends SmoothProgressBar {

    private static final float PRESS_SCALE = 0.95f;

    private final Sprite bg;
    private final ActionListener listener;
    private final MultiFrameAnimation animComplete;
    private final Sounds sounds;

    public BonusButton(
            TextureRegion regionBg,
            TextureRegion regionProgress,
            TextureRegion regionAnimComplete,
            Sounds sounds,
            ActionListener listener
    ) {
        super(regionProgress, Type.VERTICAL);
        this.sounds = sounds;
        this.listener = listener;
        bg = new Sprite(regionBg);
        animComplete = new MultiFrameAnimation(regionAnimComplete, 8, 4);
    }

    @Override
    public void setHeightProportion(float height) {
        super.setHeightProportion(height);
        bg.setHeightProportion(height);
    }

    @Override
    public void setBottom(float bottom) {
        super.setBottom(bottom);
        bg.setBottom(bottom);
    }

    @Override
    public void setRight(float right) {
        super.setRight(right);
        bg.setRight(right);
    }

    @Override
    public void setLeft(float left) {
        super.setLeft(left);
        bg.setLeft(left);
    }

    @Override
    protected void onProgressSynchronized() {
        if (progress == 1f) {
            sounds.play(BONUS_READY);
            animComplete.start(pos, getHeight() * 1.8f, 1f, true);
        } else if (progress == 0f) {
            animComplete.stop();
        }
    }

    private boolean pressed;

    public boolean touchDown(Vector2 touch) {
        if (progress == 1f && isMe(touch)) {
            pressed = true;
            setScales(PRESS_SCALE);
            return true;
        }
        return false;
    }

    public boolean touchUp(Vector2 touch) {
        if (!pressed) return false;
        pressed = false;
        setScales(1f);
        if (isMe(touch)) {
            listener.actionPerformed(this);
            setProgress(0f);
            animComplete.stop();
        }
        return true;
    }

    public void update(float deltaTime) {
        super.update(deltaTime);
        animComplete.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        bg.draw(batch);
        super.draw(batch);
        animComplete.draw(batch);
    }
}