package com.lex_portfolio.bubble_shooter.dialogs.game_over;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;

public class DialogGameOver implements ActionListener {

    private static final float BG_HEIGHT = 0.4f;
    private static final float BUTTONS_HEIGHT = 0.08f;

    private final Sprite bg;
    private final ScaledTouchUpButton btnReplay;
    private final ScaledTouchUpButton btnMenu;
    private final DialogGameOverListener listener;

    public DialogGameOver(
            TextureRegion regionBg,
            TextureRegion regionBtnReplay,
            TextureRegion regionBtnGameOver,
            DialogGameOverListener listener
    ) {
        this.listener = listener;
        bg = new Sprite(regionBg);
        bg.setHeightProportion(BG_HEIGHT);
        btnReplay = new ScaledTouchUpButton(regionBtnReplay, this);
        btnReplay.setHeightProportion(BUTTONS_HEIGHT);
        btnMenu = new ScaledTouchUpButton(regionBtnGameOver, this);
        btnMenu.setHeightProportion(BUTTONS_HEIGHT);

        final float dx = 0.5f * bg.getHalfWidth();
        final float dy = -0.9f * bg.getHalfHeight();
        btnReplay.getPos().set(bg.getPos()).add(dx, dy);
        btnMenu.getPos().set(bg.getPos()).add(-dx, dy);
    }

    public void touchDown(Vector2 touch) {
        btnReplay.touchDown(touch);
        btnMenu.touchDown(touch);
    }

    public void touchUp(Vector2 touch) {
        btnReplay.touchUp(touch);
        btnMenu.touchUp(touch);
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == btnReplay) {
            listener.onDialogGameOverReplay();
        } else if (src == btnMenu) {
            listener.onDialogGameOverMenu();
        } else {
            throw new RuntimeException();
        }
    }

    public void draw(SpriteBatch batch) {
        bg.draw(batch);
        btnReplay.draw(batch);
        btnMenu.draw(batch);
    }
}
