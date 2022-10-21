package com.lex_portfolio.bubble_shooter.screens.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.screens.arcade.select_level.SelectLevelsScreen;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.base.TypeSize;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.utils.StrBuilder;

public class StatisticsScreen extends Base2DScreen<GameBubbleShooter> {

    private static final float WORLD_HEIGHT = 1f;
    private static final float FONT_SIZE = 0.02f;

    private Sprite background;
    private Font font;

    public StatisticsScreen(GameBubbleShooter game) {
        super(game, WORLD_HEIGHT, TypeSize.HEIGHT);
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        game.getScreenManager().saveLastScreen(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        font = game.getFont("font_1.fnt");
        font.setWorldSize(FONT_SIZE);

        background = new Sprite(game.getAtlas("game.atlas").getRegion("bg_1"));
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        background.setWidthProportion(worldBounds.getWidth());
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            game.setScreen(new SelectLevelsScreen(game));
        }
        return false;
    }

    private static final String STR_HIGH_SCORE_SURVIVAL = "High Score Survival: ";
    private final StrBuilder sb = new StrBuilder();

    @SuppressWarnings("unused")
    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        font.draw(batch, sb.clean().append(STR_HIGH_SCORE_SURVIVAL).append(game.getStatistics().getSurvivalHiScore()), 0f, 0f, Align.center);
        batch.end();
    }
}
