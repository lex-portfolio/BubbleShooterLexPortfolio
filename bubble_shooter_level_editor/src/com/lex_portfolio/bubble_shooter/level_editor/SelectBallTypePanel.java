package com.lex_portfolio.bubble_shooter.level_editor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;
import com.lex_portfolio.engine.ui.panels.SelectPanelListener;
import com.lex_portfolio.engine.ui.panels.TouchUpSelectPanel;

public class SelectBallTypePanel extends TouchUpSelectPanel implements ActionListener {

    private static final float BUTTON_SIZE = 0.05f;
    private final static String REGION_RECTANGLE_RED_NAME = "selector";

    public SelectBallTypePanel(SelectPanelListener listener, TextureAtlas atlas, PoolBalls poolBalls) {
        super(listener);
        for (int i = 0; i < Ball.Type.getMaxGridableCount(); i++) {
            buttons.add(new ScaledTouchUpButton(poolBalls.getRegions()[i], this));
        }
        selector = new Sprite(atlas.findRegion(REGION_RECTANGLE_RED_NAME));
    }

    public void resize(Rect worldBounds) {
        for (int i = 0, cnt = buttons.size(); i < cnt; i++) {
            Sprite button = buttons.get(i);
            button.setSize(BUTTON_SIZE);
            button.setRight(worldBounds.getRight());
            button.setBottom(i * button.getHeight() - 0.4f);
        }
        selector.setSize(BUTTON_SIZE);
        selector.getPos().set(buttons.get(0).getPos());
    }

    @Override
    public void actionPerformed(Object src) {
        int index = buttons.indexOf((ScaledTouchUpButton) src);
        if (index == -1) throw new RuntimeException("Unknown src: " + src);
        listener.onSelect(this, src, index);
    }
}
