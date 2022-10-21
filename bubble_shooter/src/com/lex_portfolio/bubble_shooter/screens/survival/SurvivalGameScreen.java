package com.lex_portfolio.bubble_shooter.screens.survival;

import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.screens.common.GameScreen;
import com.lex_portfolio.bubble_shooter.settings.Settings;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.game_elements.spinner.SpinnerListener;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.Rnd;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.utils.StrBuilder;

import java.util.ArrayList;

public class SurvivalGameScreen extends GameScreen implements ActionListener, SpinnerListener {

    //FIELD
    private static final int MINIMUM_BALL_ROWS = 5;
    //PHYSICS
    private static final float SPEED_UP_INTERVAL = 60f;
    private static final int SPEED_UP_VALUE = 1;
    private static final int FAST_SPEED = 60;
    //DESIGN
    private static final float BASE_FONT_SIZE = 0.017f;
    private static final float HEADER_FIRST_TOP_MARGIN = 0.03f;
    private static final float HEADER_SECOND_TOP_MARGIN = 0.039f + HEADER_FIRST_TOP_MARGIN;
    private static final float HEADER_HORIZONTAL_MARGIN = 0.1f;

    private final SurvivalStageManager stageManager = new SurvivalStageManager();
    private SurvivalScreenLoader loader;
    private SurvivalFillGridManager fillGridManager;

    private Sprite upLine;

    private static final float NEXT_STAGE_DELAY = 5f;

    private int stage;
    private int frags;
    private int requiredFragsForNextStage;
    private int regularSpeed; //shiftDown per minute
    private float speedUpTimer;
    private float shiftDownTimer;
    private float nextStageTimer;

    private final StrBuilder sb = new StrBuilder();

    public SurvivalGameScreen(GameBubbleShooter game) {
        super(game);
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();

        fillGridManager = new SurvivalFillGridManager(poolBalls);
        loader = new SurvivalScreenLoader(this, stageManager, fillGridManager);

        BaseTextureAtlas atlas = game.getAtlas("game.atlas");

        upLine = new Sprite(atlas.getRegion("upper_grid_border"));
        upLine.setWidthProportion(GRID_WIDTH);

        loader.loadLastSave();
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        grid.setBottom(worldBounds.getBottom());
        upLine.setPos(0f, grid.getTop());
    }

    @Override
    public void onDialogGameOverReplay() {
        loader.loadNewGame();
        System.gc();
    }

    @Override
    protected void updateCountersAndGridPosition(float deltaTime, boolean isFlyingMajor) {
        speedUpTimer += deltaTime;
        if (speedUpTimer >= SPEED_UP_INTERVAL) {
            speedUpTimer = 0f;
            regularSpeed += SPEED_UP_VALUE;
        }
        shiftDownTimer += deltaTime;
        boolean shiftDown;
        int ballRowsCount = grid.getNotEmptyRowsCount();
        if (ballRowsCount < MINIMUM_BALL_ROWS) {
            shiftDown = shiftDownTimer >= 60f / FAST_SPEED && !isFlyingMajor;
        } else {
            shiftDown = shiftDownTimer >= 60f / regularSpeed && !isFlyingMajor;
        }
        if (shiftDown) {
            shiftDownTimer = 0f;
            fillGridManager.shiftDownAndFillRandom(
                    grid,
                    stageManager.getFillPercent(stage),
                    stageManager.getIronPercent(stage)
            );
        }
    }

    @Override
    protected void updateCompleteLevel(float deltaTime) {
        nextStageTimer += deltaTime;
        if (nextStageTimer >= NEXT_STAGE_DELAY) {
            grid.downfallAllBall(Settings.DOWNFALL_V0, Settings.DOWNFALL_A);
            loader.loadStage(stage + 1);
        }
    }

    @Override
    protected void afterHandleMajorBall() {
        super.afterHandleMajorBall();
        int currentFragBalls = deletingCount + fallDownCount;
        if (fallDownCount != 0) {
            regularSpeed -= SPEED_UP_VALUE;
            if (regularSpeed < 1) regularSpeed = 1;
        } else if (deletingCount == 0) {
            regularSpeed += SPEED_UP_VALUE;
        }
        frags += currentFragBalls;
        if ((requiredFragsForNextStage -= currentFragBalls) < 0) requiredFragsForNextStage = 0;
        if (frags > statistics.getSurvivalHiScore()) statistics.setSurvivalHiScore(frags);
    }

    @Override
    protected void onCompleteLevel() {
        grid.downfallAllBall(Settings.DOWNFALL_V0, Settings.DOWNFALL_A);
        nextStageTimer = 0f;
    }

    protected boolean isCompleteLevel() {
        return requiredFragsForNextStage == 0;
    }

    @Override
    public Ball getRandomBall() {
        Ball.Type type = grid.getGridRandomColorType();
        float a = Rnd.nextFloat(0f, 1f);
        if (a < stageManager.getIronPercent(stage) || type == null) {
            return poolBalls.obtain(Ball.Type.IRON);
        } else {
            return poolBalls.obtain(type);
        }
    }

    @Override
    protected void drawOther() {
        upperPanel.draw(batch);
        printInfo();
    }

    @Override
    protected void drawNextLevel() {
        printInfoNextLevel();
    }

    private static final String STR_SCORE = "Score: ";
    private static final String STR_SPEED = "Speed: ";
    private static final String STR_NEXT_STAGE = "Stage: ";
    private static final String STR_STAGE = "Stage: ";

    private void printInfo() {
        font.setWorldSize(BASE_FONT_SIZE);
        font.draw(batch, sb.clean().append(STR_SCORE).append(frags), grid.getLeft() + HEADER_HORIZONTAL_MARGIN, worldBounds.getTop() - HEADER_SECOND_TOP_MARGIN, Align.left);
        font.draw(batch, sb.clean().append(STR_STAGE).append(stage + 1), grid.getRight() - HEADER_HORIZONTAL_MARGIN, worldBounds.getTop() - HEADER_SECOND_TOP_MARGIN, Align.right);
        font.draw(batch, sb.clean().append(STR_SPEED).append(regularSpeed), grid.getLeft() + HEADER_HORIZONTAL_MARGIN, worldBounds.getTop() - HEADER_FIRST_TOP_MARGIN, Align.left);
    }

    private void printInfoNextLevel() {
        font.setWorldSize(BASE_FONT_SIZE * 2f);
        font.draw(batch, sb.clean().append(STR_NEXT_STAGE).append(stage + 2), worldBounds.getPos().x, worldBounds.getPos().y, Align.center);
    }

    @Override
    protected void save() {
        loader.save();
    }

    int getFrags() {
        return frags;
    }

    int getRegularSpeed() {
        return regularSpeed;
    }

    float getSpeedUpTimer() {
        return speedUpTimer;
    }

    float getShiftDownTimer() {
        return shiftDownTimer;
    }

    int getStage() {
        return stage;
    }

    ArrayList<Ball.Type> getStageColors() {
        return fillGridManager.getColors();
    }

    float getNextStageTimer() {
        return nextStageTimer;
    }

    void setNextStageTimer(float nextStageTimer) {
        this.nextStageTimer = nextStageTimer;
    }

    void setStageColors(ArrayList<Ball.Type> array) {
        fillGridManager.setColors(array);
    }

    int getRequiredFragsForNextStage() {
        return requiredFragsForNextStage;
    }

    void setFrags(int frags) {
        this.frags = frags;
    }

    void setRegularSpeed(int regularSpeed) {
        this.regularSpeed = regularSpeed;
    }

    void setStage(int stage) {
        this.stage = stage;
        this.pathTracker.setLimitTrackersCount(stageManager.getCurrentTrackersCount(stage));
    }

    void setRequiredFragsForNextStage(int requiredFragsForNextStage) {
        this.requiredFragsForNextStage = requiredFragsForNextStage;
    }

    void setSpeedUpTimer(float speedUpTimer) {
        this.speedUpTimer = speedUpTimer;
    }

    void setShiftDownTimer(float shiftDownTimer) {
        this.shiftDownTimer = shiftDownTimer;
    }
}
