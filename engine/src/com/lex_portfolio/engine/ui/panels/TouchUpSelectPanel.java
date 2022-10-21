package com.lex_portfolio.engine.ui.panels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;

import java.util.ArrayList;

public class TouchUpSelectPanel {

    protected final ArrayList<ScaledTouchUpButton> buttons = new ArrayList<>();
    protected Sprite selector;
    protected final SelectPanelListener listener;

    public TouchUpSelectPanel(SelectPanelListener listener) {
        this.listener = listener;
    }

    public boolean touchDown(Vector2 touch) {
        for (int i = 0, cnt = buttons.size(); i < cnt; i++) {
            if (buttons.get(i).touchDown(touch)) return true;
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean touchUp(Vector2 touch) {
        for (int i = 0, cnt = buttons.size(); i < cnt; i++) {
            if (buttons.get(i).touchUp(touch)) {
                selector.getPos().set(buttons.get(i).getPos());
                return true;
            }
        }
        return false;
    }

    public void update(float deltaTime) {
        for (int i = 0, cnt = buttons.size(); i < cnt; i++) buttons.get(i).update(deltaTime);
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0, cnt = buttons.size(); i < cnt; i++) buttons.get(i).draw(batch);
        selector.draw(batch);
    }
}