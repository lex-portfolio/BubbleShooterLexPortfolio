package com.lex_portfolio.bubble_shooter.screens.arcade.game;

import static com.lex_portfolio.bubble_shooter.statistics.Statistics.ARCADE_STAGE_COUNT;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.screens.arcade.game.dialogs.complete.DialogComplete;
import com.lex_portfolio.bubble_shooter.screens.arcade.game.dialogs.complete.DialogCompleteListener;
import com.lex_portfolio.bubble_shooter.screens.arcade.select_level.SelectLevelsScreen;
import com.lex_portfolio.bubble_shooter.screens.common.GameScreen;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.game_elements.spinner.SpinnerListener;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.Rnd;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.utils.Regions;
import com.lex_portfolio.engine.utils.StrBuilder;

public class ArcadeGameScreen extends GameScreen implements SpinnerListener, DialogCompleteListener {

    private static final float ACCELERATE_ALTITUDE = 0f;
    private static final float FAST_SPEED = -0.02f;
    private static final float REGULAR_SPEED = -0.002f;

    //DESIGN
    private static final float FONT_SIZE = 0.02f;
    private static final float LAST_STAGE = ARCADE_STAGE_COUNT - 1;

    private Sprite upperGridBorder;

    private DialogComplete dialogComplete;
    private Sprite bgProgressBar;
    private StarsProgressBar progressBar;
    private ArcadeScreenLoader loader;

    private int stage;
    private float starsLiveTimer;
    private float starsLiveDuration;

    private final boolean initLastSave;
    private int requestStage;

    private final StrBuilder sb = new StrBuilder();

    public ArcadeGameScreen(GameBubbleShooter game) {
        super(game);
        initLastSave = true;
    }

    public ArcadeGameScreen(GameBubbleShooter game, int stage) {
        super(game);
        initLastSave = false;
        requestStage = stage;
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        BaseTextureAtlas atlas = game.getAtlas("game.atlas");

        dialogComplete = new DialogComplete(atlas.getRegion("bg_dialog_win"), atlas.getRegion("btn_replay"), atlas.getRegion("btn_next"), this);

        upperGridBorder = new Sprite(atlas.getRegion("upper_grid_border"));
        upperGridBorder.setWidthProportion(GRID_WIDTH);

        final float progressBarX = 0f;
        final float progressBarY = 0.445f;
        bgProgressBar = new Sprite(atlas.getRegion("bg_progress_bar"));
        bgProgressBar.setHeightProportion(0.06f);
        bgProgressBar.setPos(progressBarX, progressBarY);

        progressBar = new StarsProgressBar(atlas.getRegion("progress_bar"), Regions.split(atlas.getRegion("stars_progress_bar"), 1, 2));

        loader = new ArcadeScreenLoader(this);
        if (initLastSave) {
            loader.loadLastSave();
        } else {
            loader.loadStage(requestStage);
        }
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        super.resize(worldBounds);
        progressBar.setWidth(0.75f * bgProgressBar.getWidth());
        progressBar.setHeight(0.25f * bgProgressBar.getHeight());
        float dx = 0.14f * bgProgressBar.getHalfWidth();
        float dy = -0.13f * bgProgressBar.getHalfHeight();
        progressBar.setPos(bgProgressBar.getPos().x + dx, bgProgressBar.getPos().y + dy);
    }

    @Override
    protected void touchDownCompleteLevel(Vector2 touch) {
        dialogComplete.touchDown(touch);
    }

    @Override
    protected void touchUpCompleteLevel(Vector2 touch) {
        dialogComplete.touchUp(touch);
    }

    @Override
    protected void updateCountersAndGridPosition(float deltaTime, boolean isFlyingMajor) {
        starsLiveTimer -= deltaTime;
        if (starsLiveTimer < 0) starsLiveTimer = 0f;
        progressBar.setProgress(starsLiveTimer / starsLiveDuration);

        int f = grid.findLowerNotEmptyRow();
        if (f != -1 && grid.getRowBottom(f) > ACCELERATE_ALTITUDE) {
            grid.setBottom(grid.getBottom() + FAST_SPEED * deltaTime);
        } else {
            grid.setBottom(grid.getBottom() + REGULAR_SPEED * deltaTime);
        }
        upperGridBorder.setBottom(grid.getTop());
    }

    private void checkBalls() {
        for (int i = 0; i < cartridge.length; i++) {
            if (cartridge[i] != null && !isValidBall(cartridge[i])) {
                poolBalls.free(cartridge[i]);
                cartridge[i] = getRandomBall();
                cartridge[i].setToCartridge();
                cartridge[i].setScales(CARTRIDGE_BALLS_SIZE_PERCENT);
            }
        }
    }

    @Override
    protected void afterHandleMajorBall() {
        super.afterHandleMajorBall();
        if (fallDownCount != 0) {
            float delta = starsLiveDuration - starsLiveTimer;
            if (fallDownCount > 10) {
                starsLiveTimer = starsLiveTimer + delta;
            } else if (fallDownCount > 8) {
                starsLiveTimer = starsLiveTimer + delta / 2;
            } else if (fallDownCount > 6) {
                starsLiveTimer = starsLiveTimer + delta / 3;
            } else if (fallDownCount > 4) {
                starsLiveTimer = starsLiveTimer + delta / 4;
            } else if (fallDownCount >= 3) {
                starsLiveTimer = starsLiveTimer + delta / 5;
            }
        }
        checkBalls();
    }

    protected boolean isCompleteLevel() {
        return grid.getGridRandomColorType() == null;
    }

    @Override
    protected void onCompleteLevel() {
        statistics.setStageStars(stage, progressBar.getsStarsCount());
        int nextStage = stage + 1;
        if (nextStage > statistics.getLastAvailableStage())
            statistics.setLastAvailableStage(nextStage);
    }

    @Override
    protected void drawOther() {
        upperGridBorder.draw(batch);
        upperPanel.draw(batch);
        bgProgressBar.draw(batch);
        progressBar.draw(batch);
    }

    @Override
    protected void drawNextLevel() {
        dialogComplete.draw(batch);
        printInfoLevelYouWin();
    }

    @Override
    public void onDialogGameOverReplay() {
        loader.loadStageFromAsset(stage);
        System.gc();
    }

    @Override
    public void onDialogCompleteReplay() {
        loader.loadStageFromAsset(stage);
        System.gc();
    }

    @Override
    public void onDialogCompleteNextLevel() {
        if (stage == LAST_STAGE) {
            game.setScreen(new SelectLevelsScreen(game));
        } else {
            loader.loadStageFromAsset(stage + 1);
        }
    }

    private static final float IRON_PERCENT = 0.15f;

    @Override
    public Ball getRandomBall() {
        Ball.Type type = grid.getGridRandomColorType();
        float a = Rnd.nextFloat(0f, 1f);
        if (a < IRON_PERCENT || type == null) {
            return poolBalls.obtain(Ball.Type.IRON);
        } else {
            return poolBalls.obtain(type);
        }
    }

    private boolean isValidBall(Ball ball) {
        Ball.Type type = ball.getType();
        if (type.isColored()) {
            return grid.containsBallColor(type);
        } else if (type == Ball.Type.IRON || type == Ball.Type.BOMB || type == Ball.Type.FIRE) {
            return true;
        } else {
            throw new IllegalStateException("type: " + type);
        }
    }

    private static final String STR_LEVEL = "Level: ";

    private static final String STR_YOU_WIN = "YOU WIN";
    private static final String STR_COMPLETE_WIN = "COMPLETE VICTORY";

    private void printInfoLevelYouWin() {
        font.setWorldSize(FONT_SIZE);
        font.draw(batch, sb.clean().append(STR_YOU_WIN), 0f, 0f, Align.center);
        if (stage == LAST_STAGE) {
            font.draw(batch, sb.clean().append(STR_COMPLETE_WIN), 0f, -0.05f, Align.center);
            return;
        }
        font.draw(batch, sb.clean().append(STR_LEVEL).append(stage), 0f, -0.05f, Align.center);
    }

    @Override
    protected void save() {
        loader.save(this);
    }

    public int getStage() {
        return stage;
    }

    float getStarsLiveTimer() {
        return starsLiveTimer;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    void setStarsLiveDuration(float starsLiveDuration) {
        this.starsLiveDuration = starsLiveDuration;
    }

    void setStarsLiveTimer(float starsLiveTimer) {
        this.starsLiveTimer = starsLiveTimer;
    }
}
