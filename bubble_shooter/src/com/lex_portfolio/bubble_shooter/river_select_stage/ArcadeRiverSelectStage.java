package com.lex_portfolio.bubble_shooter.river_select_stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.statistics.Statistics;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.river.RiverVertical;

import static com.lex_portfolio.bubble_shooter.river_select_stage.ButtonLevel.Type.LEFT;
import static com.lex_portfolio.bubble_shooter.river_select_stage.ButtonLevel.Type.RIGHT;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class ArcadeRiverSelectStage extends RiverVertical {

    public static final int STAGES_TO_PAGE = 6;
    private static final Vector2 pos1 = new Vector2(-0.02f, -0.49f);
    private static final Vector2 pos2 = new Vector2(-0.32f, -0.21f);
    private static final Vector2 pos3 = new Vector2(0.08f, -0.076f);
    private static final Vector2 pos4 = new Vector2(0.28f, 0.078f);
    private static final Vector2 pos5 = new Vector2(-0.25f, 0.18f);
    private static final Vector2 pos6 = new Vector2(0.26f, 0.32f);

    private final Vector2[] buttonsBasePos = {pos1, pos2, pos3, pos4, pos5, pos6, pos1, pos2, pos3, pos4, pos5, pos6};
    private final ButtonLevel[] buttons;
    private final float buttonsHeightProportion;
    private final float starsHeightProportion;
    private final float markerHeightProportion;

    public ArcadeRiverSelectStage(
            TextureRegion regionRiver,
            TextureRegion[] regionsButtonsLeft,
            TextureRegion[] regionsButtonsRight,
            TextureRegion regionMarkerLeft,
            TextureRegion regionMarkerRight,
            TextureRegion[] regionLeftStars,
            TextureRegion[] regionRightStars,
            Font font,
            int lastPage,
            Statistics statistics,
            SelectStageListener listener
    ) {
        super(regionRiver, lastPage);
        buttons = new ButtonLevel[STAGES_TO_PAGE * 2];
        for (int i = 0; i < buttons.length; i++) {
            if (i == 0 || i == 3 || i == 5 || i == 6 || i == 9 || i == 11) {
                buttons[i] = new ButtonLevel(
                        regionsButtonsRight, regionRightStars, regionMarkerRight, font, statistics, RIGHT, listener
                );
            } else {
                buttons[i] = new ButtonLevel(
                        regionsButtonsLeft, regionLeftStars, regionMarkerLeft, font, statistics, LEFT, listener
                );
            }
        }
        float regionRiverHeight = regionRiver.getRegionHeight();
        buttonsHeightProportion = regionsButtonsLeft[0].getRegionHeight() / regionRiverHeight;
        starsHeightProportion = regionLeftStars[0].getRegionHeight() / regionRiverHeight;
        markerHeightProportion = regionMarkerLeft.getRegionHeight() / regionRiverHeight;
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        float pageBgSpriteHeight = getPageBgSprite().getHeight();
        float buttonsHeight = pageBgSpriteHeight * buttonsHeightProportion;
        float starsHeight = pageBgSpriteHeight * starsHeightProportion;
        float markerHeight = pageBgSpriteHeight * markerHeightProportion;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setHeightAllProportions(buttonsHeight, markerHeight, starsHeight);
        }
    }

    private boolean buttonPressed;

    @Override
    public void touchDown(Vector2 touch, long eventTime) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].touchDown(touch)) {
                buttonPressed = true;
                return;
            }
        }
        super.touchDown(touch, eventTime);
    }

    @Override
    public void touchMove(Vector2 touch, long eventTime) {
        if (!buttonPressed) super.touchMove(touch, eventTime);
    }

    @Override
    public void touchUp(Vector2 touch, long eventTime) {
        if (buttonPressed) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].touchUp(touch);
            }
            buttonPressed = false;
        } else {
            super.touchUp(touch, eventTime);
        }
    }

    private final Vector2 btnPos = new Vector2();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Sprite pageSprite = getPageBgSprite();
        float pageHeight = pageSprite.getHeight();

        final int half = buttons.length / 2;
        for (int i = 0; i < buttons.length; i++) {
            int stage = getPage() * STAGES_TO_PAGE + i;
            btnPos.set(buttonsBasePos[i]).scl(pageSprite.getWidth(), pageSprite.getHeight()).add(pageSprite.getPos());
            if (i >= half) btnPos.add(0f, pageHeight);
            buttons[i].update(stage, btnPos);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        for (int i = 0; i < buttons.length; i++) buttons[i].draw(batch);
    }
}