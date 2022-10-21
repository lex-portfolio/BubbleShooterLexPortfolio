package com.lex_portfolio.bubble_shooter.screens.arcade.game.dialogs.complete;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;

public class DialogComplete implements ActionListener {

    private static final float BG_HEIGHT = 0.5f;
    private static final float BUTTONS_HEIGHT = 0.08f;

    private final Sprite bg;
    private final ScaledTouchUpButton btnReplay;
    private final ScaledTouchUpButton btnNextLevel;
    private final DialogCompleteListener listener;

    public DialogComplete(TextureRegion regionBg, TextureRegion regionBtnReplay, TextureRegion regionBtnNextLevel, DialogCompleteListener listener) {
        this.listener = listener;
        bg = new Sprite(regionBg);
        bg.setHeightProportion(BG_HEIGHT);
        btnReplay = new ScaledTouchUpButton(regionBtnReplay, this);
        btnReplay.setHeightProportion(BUTTONS_HEIGHT);
        btnNextLevel = new ScaledTouchUpButton(regionBtnNextLevel, this);
        btnNextLevel.setHeightProportion(BUTTONS_HEIGHT);

        final float dx = 0.5f * bg.getHalfWidth();
        final float dy = -0.9f * bg.getHalfHeight();
        btnNextLevel.getPos().set(bg.getPos()).add(dx, dy);
        btnReplay.getPos().set(bg.getPos()).add(-dx, dy);
    }

    @SuppressWarnings("unused")
    public void touchDown(Vector2 touch) {
        btnReplay.touchDown(touch);
        btnNextLevel.touchDown(touch);
    }

    @SuppressWarnings("unused")
    public void touchUp(Vector2 touch) {
        btnReplay.touchUp(touch);
        btnNextLevel.touchUp(touch);
    }

    @SuppressWarnings("unused")
    public void draw(SpriteBatch batch) {
        bg.draw(batch);
        btnReplay.draw(batch);
        btnNextLevel.draw(batch);
    }

    @SuppressWarnings("unused")
    @Override
    public void actionPerformed(Object src) {
        if (src == btnReplay) {
            listener.onDialogCompleteReplay();
        } else if (src == btnNextLevel) {
            listener.onDialogCompleteNextLevel();
        } else {
            throw new RuntimeException();
        }
    }
}
