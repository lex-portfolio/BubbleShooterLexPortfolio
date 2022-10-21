package com.lex_portfolio.engine.ui.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;

public class ScaledTouchUpButton extends Sprite {

    private final ActionListener listener;
    protected final float pressScale;

    public ScaledTouchUpButton(TextureRegion region, ActionListener listener) {
        this(region, listener, 0.95f);
    }


    public ScaledTouchUpButton(TextureRegion region, ActionListener listener, float pressScale) {
        super(region);
        this.listener = listener;
        this.pressScale = pressScale;
    }

    protected boolean pressed;

    public boolean touchDown(Vector2 touch) {
        if (isMe(touch)) {
            pressed = true;
            setScales(pressScale);
            return true;
        }
        return false;
    }

    public boolean touchUp(Vector2 touch) {
        if (pressed) {
            pressed = false;
            setScales(1f);
            if (isMe(touch)) {
                listener.actionPerformed(this);
            }
            return true;
        }
        return false;
    }
}
