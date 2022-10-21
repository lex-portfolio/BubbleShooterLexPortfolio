package com.lex_portfolio.bubble_shooter.screens.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.screens.arcade.select_level.SelectLevelsScreen;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.base.TypeSize;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;

public class HelpScreen extends Base2DScreen<GameBubbleShooter> implements ActionListener {

    private static final float WORLD_HEIGHT = 1f;
    private static final float BUTTON_BACK_HEIGHT = 0.08f;

    private Sprite background;
    private Sprite text;
    private ScaledTouchUpButton btnBack;

    public HelpScreen(GameBubbleShooter game) {
        super(game, WORLD_HEIGHT, TypeSize.HEIGHT);
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        game.getScreenManager().saveLastScreen(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        BaseTextureAtlas atlas = game.getAtlas("game.atlas");
        background = new Sprite(atlas.getRegion("bg_1"));
        text = new Sprite(atlas.getRegion("text_help"));
        text.setHeightProportion(WORLD_HEIGHT);
        btnBack = new ScaledTouchUpButton(atlas.getRegion("btn_back"), this);
        btnBack.setHeightProportion(BUTTON_BACK_HEIGHT);
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        background.setWidthProportion(worldBounds.getWidth());
        btnBack.setLeft(worldBounds.getLeft());
        btnBack.setTop(worldBounds.getTop());
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchDown(Vector2 touch, long eventTime) {
        btnBack.touchDown(touch);
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchUp(Vector2 touch, long eventTime) {
        btnBack.touchUp(touch);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            backProcess();
        }
        return false;
    }

    private void backProcess() {
        game.setScreen(new SelectLevelsScreen(game));
    }

    @SuppressWarnings("unused")
    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        background.draw(batch);
        text.draw(batch);
        btnBack.draw(batch);
        batch.end();
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == btnBack) {
            backProcess();
        } else {
            throw new RuntimeException("Unknown src: " + src);
        }
    }
}