package com.lex_portfolio.bubble_shooter.screens.common;

import static com.lex_portfolio.bubble_shooter.settings.Settings.BOMB_BALL_V;
import static com.lex_portfolio.bubble_shooter.settings.Settings.COLOR_BALL_V;
import static com.lex_portfolio.bubble_shooter.settings.Settings.FIRE_BALL_V;
import static com.lex_portfolio.bubble_shooter.settings.Settings.IRON_BALL_V;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.COLLISION_COLOR;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.COLLISION_IRON;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.DOWNFALL;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.EXPLOSION_BOMB;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.EXPLOSION_COLOR;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.EXPLOSION_FIREBALL;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.SHOOT_BOMB;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.SHOOT_COLORED;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.SHOOT_FIREBALL;
import static com.lex_portfolio.bubble_shooter.sounds.Sounds.Type.SHOOT_IRON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.BallsGrid;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.bubble_shooter.buttons.BonusButton;
import com.lex_portfolio.bubble_shooter.dialogs.game_over.DialogGameOver;
import com.lex_portfolio.bubble_shooter.dialogs.game_over.DialogGameOverListener;
import com.lex_portfolio.bubble_shooter.dialogs.settings.DialogSettings;
import com.lex_portfolio.bubble_shooter.dialogs.settings.DialogSettingsListener;
import com.lex_portfolio.bubble_shooter.path_tracker.PathTracker;
import com.lex_portfolio.bubble_shooter.screens.arcade.select_level.SelectLevelsScreen;
import com.lex_portfolio.bubble_shooter.settings.Settings;
import com.lex_portfolio.bubble_shooter.sounds.Sounds;
import com.lex_portfolio.bubble_shooter.statistics.Statistics;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.base.TypeSize;
import com.lex_portfolio.engine.game_elements.spinner.Spinner;
import com.lex_portfolio.engine.game_elements.spinner.SpinnerListener;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.Rnd;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.hex_grid.GridView;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;
import com.lex_portfolio.engine.utils.Regions;
import com.lex_portfolio.engine.utils.StrBuilder;
import com.lex_portfolio.engine.view.Drawer;

@SuppressWarnings("ForLoopReplaceableByForEach")
public abstract class GameScreen extends Base2DScreen<GameBubbleShooter> implements PathTracker.CollisionChecker, SpinnerListener, BallsGridScreen, ActionListener, DialogSettingsListener, DialogGameOverListener {

    private static final boolean LOG_FPS = false;
    private static final float FPS_INTERVAL = 1f;

    private static final boolean DRAW_HEX_GRID = false;
    private static final boolean DRAW_LINES = false;
    private static final float WORLD_HEIGHT = 1f;
    protected static final float CARTRIDGE_BALLS_SIZE_PERCENT = 0.55f;
    private static final int UPDATE_COUNT = 10;
    public static final float GRID_WIDTH = 0.54636335f;
    private static final float UPPER_BUTTONS_HEIGHT = 0.06f;
    private static final float BUTTONS_HEIGHT = 0.08f;

    public enum State {
        WAITING, FLYING_MAJOR_BALL, ROTATION_SPINNER, FLYING_MAJOR_AND_ROTATION_SPINNER, GAME_OVER, COMPLETE_LEVEL, SETTINGS
    }

    private State state;

    protected PoolBalls poolBalls;
    protected final Statistics statistics;
    private ShapeRenderer shapeRenderer;
    protected BallsGrid grid;
    private GridView gridView;

    private Ball flyingMajorBall;
    protected final Ball[] cartridge = new Ball[3];

    protected Font font;

    protected PathTracker pathTracker;
    private Spinner spinner;
    private float disengagementBorder;

    private Sprite background;
    protected Sprite upperPanel;
    private Sprite leftBorder;
    private Sprite rightBorder;
    private Sprite warningLine;

    private ScaledTouchUpButton btnBack;
    private ScaledTouchUpButton btnSettings;

    private BonusButton btnFireball;
    private BonusButton btnBomb;

    private DialogSettings dialogSettings;
    private DialogGameOver dialogGameOver;

    public GameScreen(GameBubbleShooter game) {
        super(game, WORLD_HEIGHT, TypeSize.HEIGHT);
        statistics = game.getStatistics();
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        game.getScreenManager().saveLastScreen(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        poolBalls = game.getPoolBalls();
        font = game.getFont("font_level.fnt");

        Settings settings = game.getSettings();
        BaseTextureAtlas atlas = game.getAtlas("game.atlas");
        dialogSettings = new DialogSettings(
                atlas.getRegion("bg_dialog_settings"),
                atlas.getRegion("btn_music"),
                atlas.getRegion("btn_sound"),
                atlas.getRegion("btn_close"),
                font,
                settings.isMusicOn(),
                settings.isSoundOn(),
                this
        );
        dialogGameOver = new DialogGameOver(
                atlas.getRegion("bg_dialog_game_over"),
                atlas.getRegion("btn_replay"),
                atlas.getRegion("btn_menu"),
                this
        );

        background = new Sprite(atlas.getRegion("bg_1"));

        upperPanel = new Sprite(atlas.getRegion("upper_panel"));
        upperPanel.setWidthProportion(GRID_WIDTH * 1.03f);

        TextureRegion regionBorderGrid = atlas.getRegion("border_grid");
        leftBorder = new Sprite(regionBorderGrid);
        rightBorder = new Sprite(regionBorderGrid);

        pathTracker = new PathTracker(poolBalls.getRegions(), this, Settings.MAX_PATH_TRACKERS_COUNT);

        spinner = new Spinner(
                atlas.getRegion("spinner"),
                Settings.SPINNER_POS,
                Settings.SPINNER_ECCENTRICITY,
                Settings.SPINNER_HEIGHT,
                Settings.SPINNER_RADIUS_PERCENT,
                this
        );
        disengagementBorder = spinner.getTop();
        warningLine = new Sprite(atlas.getRegion("stop_line"));
        warningLine.setWidthProportion(GRID_WIDTH);
        warningLine.getPos().y = spinner.getTop();

        btnBack = new ScaledTouchUpButton(atlas.getRegion("btn_back"), this);
        btnBack.setHeightProportion(UPPER_BUTTONS_HEIGHT);
        btnSettings = new ScaledTouchUpButton(atlas.getRegion("btn_settings"), this);
        btnSettings.setHeightProportion(UPPER_BUTTONS_HEIGHT);

        TextureRegion[] regionsBtnFire = Regions.split(atlas.getRegion("btn_fireball"), 1, 2);
        TextureRegion regionsBtnFireBg = regionsBtnFire[0];
        TextureRegion regionBtnFireProgress = regionsBtnFire[1];
        TextureRegion regionCompleteAnimation = atlas.getRegion("animation_bonus_1");
        Sounds sounds = game.getSounds();
        btnFireball = new BonusButton(regionsBtnFireBg, regionBtnFireProgress, regionCompleteAnimation, sounds, this);
        btnFireball.setHeightProportion(BUTTONS_HEIGHT);

        TextureRegion[] regionsBtnBomb = Regions.split(atlas.getRegion("btn_bomb"), 1, 2);
        TextureRegion regionsBtnBombBg = regionsBtnBomb[0];
        TextureRegion regionBtnBombProgress = regionsBtnBomb[1];
        btnBomb = new BonusButton(regionsBtnBombBg, regionBtnBombProgress, regionCompleteAnimation, sounds, this);
        btnBomb.setHeightProportion(BUTTONS_HEIGHT);

        if (DRAW_HEX_GRID) shapeRenderer = new ShapeRenderer();
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        if (DRAW_HEX_GRID) shapeRenderer.setProjectionMatrix(matWorldToGl);
        float worldTop = worldBounds.getTop();
        background.setWidthProportion(worldBounds.getWidth());
        background.setBottom(worldBounds.getBottom());
        upperPanel.setTop(worldTop + 0.002f);

        final float BORDER_HEIGHT = 0.8f;
        final float BORDER_MARGIN = 0.1f;
        final float BORDER_WIDTH = 0.005f;
        float worldHeight = worldBounds.getHeight();
        leftBorder.setHeight(worldHeight * BORDER_HEIGHT);
        leftBorder.setWidth(BORDER_WIDTH);
        leftBorder.setTop(worldTop - BORDER_MARGIN);
        leftBorder.setRight(-GRID_WIDTH * 0.5f);
        rightBorder.setHeight(worldHeight * BORDER_HEIGHT);
        rightBorder.setWidth(BORDER_WIDTH);
        rightBorder.setTop(worldTop - BORDER_MARGIN);
        rightBorder.setLeft(GRID_WIDTH * 0.5f);

        final float UPPER_BUTTONS_TOP_MARGIN = 0.025f;
        final float UPPER_BUTTONS_BORDER_MARGIN = 0.038f;
        btnBack.setLeft(upperPanel.getLeft() + UPPER_BUTTONS_BORDER_MARGIN);
        btnBack.setTop(upperPanel.getTop() - UPPER_BUTTONS_TOP_MARGIN);
        btnSettings.setRight(upperPanel.getRight() - UPPER_BUTTONS_BORDER_MARGIN);
        btnSettings.setTop(upperPanel.getTop() - UPPER_BUTTONS_TOP_MARGIN);

        final float BOTTOM_BUTTONS_BOTTOM_MARGIN = 0.02f;
        btnFireball.setBottom(worldBounds.getBottom() + BOTTOM_BUTTONS_BOTTOM_MARGIN);
        btnFireball.setLeft(-GRID_WIDTH * 0.5f);

        btnBomb.setBottom(worldBounds.getBottom() + BOTTOM_BUTTONS_BOTTOM_MARGIN);
        btnBomb.setRight(GRID_WIDTH * 0.5f);
    }

    @SuppressWarnings({"StatementWithEmptyBody", "unused"})
    @Override
    protected void touchDown(Vector2 touch, long eventTime) {
        switch (state) {
            case WAITING:
                if (touchDownUI(touch)) return;
                if (spinner.isMe(touch)) {
                    spinner.starRotateLong();
                    setState(State.ROTATION_SPINNER);
                } else if (touch.y > disengagementBorder) {
                    engage(touch);
                }
                break;
            case FLYING_MAJOR_BALL:
                if (spinner.isMe(touch)) {
                    spinner.starRotateLong();
                    setState(State.FLYING_MAJOR_AND_ROTATION_SPINNER);
                } else if (touch.y > disengagementBorder) {
                    arm(touch);
                }
                break;
            case ROTATION_SPINNER:
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                if (spinner.isMe(touch)) {
                    //ignored
                } else if (touch.y > disengagementBorder) {
                    arm(touch);
                }
                break;
            case GAME_OVER:
                dialogGameOver.touchDown(touch);
                break;
            case COMPLETE_LEVEL:
                touchDownCompleteLevel(touch);
                break;
            case SETTINGS:
                btnBack.touchDown(touch);
                dialogSettings.touchDown(touch);
                break;
            default:
                throw new RuntimeException("Unknown state = " + state);
        }
    }

    private boolean isTouchUI;

    private boolean touchDownUI(Vector2 touch) {
        if (btnSettings.touchDown(touch)) {
            isTouchUI = true;
            return true;
        } else if (btnBack.touchDown(touch)) {
            isTouchUI = true;
            return true;
        } else if (btnFireball.touchDown(touch)) {
            isTouchUI = true;
            return true;
        } else if (btnBomb.touchDown(touch)) {
            isTouchUI = true;
            return true;
        } else {
            return false;
        }
    }

    protected void touchDownCompleteLevel(Vector2 touch) {
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchMove(Vector2 touch, long eventTime) {
        switch (state) {
            case WAITING:
                if (touchMoveUI()) return;
                if (touch.y > disengagementBorder) {
                    engage(touch);
                } else {
                    disengage();
                }
                break;
            case FLYING_MAJOR_BALL:
            case ROTATION_SPINNER:
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                if (touch.y > disengagementBorder) {
                    arm(touch);
                } else {
                    disengage();
                }
                break;
            case GAME_OVER:
            case COMPLETE_LEVEL:
            case SETTINGS:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private boolean touchMoveUI() {
        return isTouchUI;
    }

    private final Vector2 majorBallV = new Vector2();

    private void shootMajorBall(Vector2 touch) {
        int upperIndex = spinner.getUpperIndex();
        if (flyingMajorBall != null) throw new RuntimeException();
        flyingMajorBall = cartridge[upperIndex];
        cartridge[upperIndex] = null;
        deletingCount = 0;
        fallDownCount = 0;
        majorBallV.set(touch).sub(flyingMajorBall.getPos()).nor();
        Ball.Type type = flyingMajorBall.getType();
        if (type.isColored()) {
            game.getSounds().play(SHOOT_COLORED);
            majorBallV.scl(COLOR_BALL_V);
        } else if (type == Ball.Type.IRON) {
            game.getSounds().play(SHOOT_IRON);
            majorBallV.scl(IRON_BALL_V);
        } else if (type == Ball.Type.BOMB) {
            game.getSounds().play(SHOOT_BOMB);
            majorBallV.scl(BOMB_BALL_V);
        } else if (type == Ball.Type.FIRE) {
            game.getSounds().play(SHOOT_FIREBALL);
            majorBallV.scl(FIRE_BALL_V);
        } else {
            throw new IllegalStateException("type: " + type);
        }
        flyingMajorBall.majorFlying(majorBallV);
        flyingMajorBall.setSize(getGridBallSize());
        spinner.startShortRotate();
        setState(State.FLYING_MAJOR_AND_ROTATION_SPINNER);
    }


    @SuppressWarnings("unused")
    @Override
    protected void touchUp(Vector2 touch, long eventTime) {
        switch (state) {
            case WAITING:
                if (touchUpUI(touch)) return;
                if (touch.y > disengagementBorder) {
                    shootMajorBall(touch);
                }
            case FLYING_MAJOR_BALL:
            case ROTATION_SPINNER:
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                disengage();
                break;
            case GAME_OVER:
                dialogGameOver.touchUp(touch);
                break;
            case COMPLETE_LEVEL:
                touchUpCompleteLevel(touch);
                break;
            case SETTINGS:
                btnBack.touchUp(touch);
                dialogSettings.touchUp(touch);
                break;
            default:
                throw new RuntimeException("Unknown state = " + state);
        }
    }

    private boolean touchUpUI(Vector2 touch) {
        if (btnSettings.touchUp(touch)) {
            isTouchUI = false;
            return true;
        } else if (btnBack.touchUp(touch)) {
            isTouchUI = false;
            return true;
        } else if (btnFireball.touchUp(touch)) {
            isTouchUI = false;
            return true;
        } else if (btnBomb.touchUp(touch)) {
            isTouchUI = false;
            return true;
        } else {
            return false;
        }
    }

    protected void touchUpCompleteLevel(Vector2 touch) {
    }

    private void arm(Vector2 touch) {
        Ball ball = cartridge[spinner.getUpperIndex()];
        Ball.Type type = ball.getType();
        if (type.isColored() || type == Ball.Type.IRON || type == Ball.Type.BOMB) {
            pathTracker.arm(ball.getPos(), touch, ball.getType().ordinal(), PathTracker.Type.MIRROR);
        } else if (type == Ball.Type.FIRE) {
            pathTracker.arm(ball.getPos(), touch, ball.getType().ordinal(), PathTracker.Type.THROUGH);
        } else {
            throw new IllegalStateException("type: " + type);
        }
    }

    private void engage(Vector2 touch) {
        Ball ball = cartridge[spinner.getUpperIndex()];
        Ball.Type type = ball.getType();
        if (type.isColored() || type == Ball.Type.IRON || type == Ball.Type.BOMB) {
            pathTracker.engage(ball.getPos(), touch, ball.getType().ordinal(), PathTracker.Type.MIRROR);
        } else if (type == Ball.Type.FIRE) {
            pathTracker.engage(ball.getPos(), touch, ball.getType().ordinal(), PathTracker.Type.THROUGH);
        } else {
            throw new IllegalStateException("type: " + type);
        }
    }

    private void disengage() {
        pathTracker.disengage();
    }

    @SuppressWarnings("unused")
    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        draw();
    }

    public void setState(State state) {
//        System.err.println("Change state " + this.state + " -> " + state);
        this.state = state;
    }

    public State getState() {
        return state;
    }

    private final StrBuilder sbFPS = new StrBuilder(2);
    private float fpsTimer;

    private void updateFPSLogger(float deltaTime) {
        if (!LOG_FPS) return;
        fpsTimer += deltaTime;
        if (fpsTimer >= FPS_INTERVAL) {
            fpsTimer = 0f;
            int fps = (int) (1f / deltaTime);
            sbFPS.clean().append(fps);
            if (fps <= 50) {
                Gdx.app.error("FPS", sbFPS.toString());
            } else {
                Gdx.app.log("FPS", sbFPS.toString());
            }
        }
    }

    private void updateUI(float deltaTime) {
        btnFireball.update(deltaTime);
        btnBomb.update(deltaTime);
    }

    private void updateEffects(float deltaTime) {
        poolBalls.getEffectBallExplosionPool().updateActiveObjectsAndFreeAllDestroyed(deltaTime);
        poolBalls.getDebugMarkerBallDeletedPool().updateActiveObjectsAndFreeAllDestroyed(deltaTime);
        poolBalls.getDebugMarkersBallDownfallPool().updateActiveObjectsAndFreeAllDestroyed(deltaTime);
    }

    private void update(float deltaTime) {
        updateFPSLogger(deltaTime);
        updateBackTimer(deltaTime);
        updateEffects(deltaTime);
        updateUI(deltaTime);
        spinner.update(deltaTime);
        switch (state) {
            case WAITING:
            case ROTATION_SPINNER:
                updateCountersAndGridPosition(deltaTime, false);
                if (isGameOver()) setState(State.GAME_OVER);
                poolBalls.updateAndFreeAllDestroyed(deltaTime, grid);
                break;
            case FLYING_MAJOR_BALL:
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                updateCountersAndGridPosition(deltaTime, true);
                if (isGameOver()) {
                    poolBalls.free(flyingMajorBall);
                    flyingMajorBall = null;
                    setState(State.GAME_OVER);
                    return;
                }
                deltaTime /= UPDATE_COUNT;
                for (int i = 0; i < UPDATE_COUNT; i++) {
                    if (updateFlyingMajorBall(deltaTime)) break;
                }
                break;
            case GAME_OVER:
                poolBalls.updateAndFreeAllDestroyed(deltaTime, grid);
                break;
            case COMPLETE_LEVEL:
                updateCompleteLevel(deltaTime);
                poolBalls.updateAndFreeAllDestroyed(deltaTime, grid);
                break;
            case SETTINGS:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    protected int fallDownCount;
    protected int deletingCount;

    private boolean updateFlyingMajorBall(float deltaTime) {
        poolBalls.updateAndFreeAllDestroyed(deltaTime, grid);
        Ball.Type type = flyingMajorBall.getType();
        if (type.isColored()) {
            Cell majorBallCell = getStopCellMirrorFlyMajorBall();
            if (majorBallCell == null) return false;
            deletingCount = grid.deleteColorBlock(majorBallCell).size();
            fallDownCount = grid.downfallNonUpperPathBalls(Settings.DOWNFALL_V0, Settings.DOWNFALL_A, 180f);
            if (fallDownCount != 0) {
                game.getSounds().play(DOWNFALL);
            } else if (deletingCount != 0) {
                game.getSounds().play(EXPLOSION_COLOR);
            } else {
                game.getSounds().play(COLLISION_COLOR);
            }
        } else if (type == Ball.Type.IRON) {
            Cell majorBallCell = getStopCellMirrorFlyMajorBall();
            if (majorBallCell == null) return false;
            deletingCount = 0;
            fallDownCount = 0;
            game.getSounds().play(COLLISION_IRON);
        } else if (type == Ball.Type.BOMB) {
            Cell majorBallCell = getStopCellMirrorFlyMajorBall();
            if (majorBallCell == null) return false;
            deletingCount = grid.boomBalls(majorBallCell);
            fallDownCount = grid.downfallNonUpperPathBalls(Settings.DOWNFALL_V0, Settings.DOWNFALL_A, 180f);
            game.getSounds().play(EXPLOSION_BOMB);
        } else if (type == Ball.Type.FIRE) {
            boolean result = updateFireMajorBall();
            fallDownCount += grid.downfallNonUpperPathBalls(Settings.DOWNFALL_V0, Settings.DOWNFALL_A, 180f);
            if (!result) return false;
        } else {
            throw new IllegalStateException("type: " + type);
        }
        flyingMajorBall = null;
        switch (state) {
            case FLYING_MAJOR_BALL:
                setState(State.WAITING);
                pathTracker.engageIfArm();
                break;
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                setState(State.ROTATION_SPINNER);
                break;
            default:
                throw new RuntimeException("state = " + state);
        }
        afterHandleMajorBall();
        if (isCompleteLevel()) {
            setState(State.COMPLETE_LEVEL);
            onCompleteLevel();
        }
        return true;
    }

    private Cell getStopCellMirrorFlyMajorBall() {
        Cell majorBallCell = grid.getCell(flyingMajorBall.getPos());
        if (majorBallCell == null) { //Перелёт верхней границы сетки
            majorBallCell = flyingMajorBall.flashBackToFirstCell(grid.r() * 0.1f, grid, false);
        } else {
            if (DRAW_HEX_GRID) gridView.select(majorBallCell);
        }
        Cell collisionBallCell = grid.getCollisionBallCell(flyingMajorBall, getGridBallSize() * Settings.COLLISION_PERCENT);
        if (collisionBallCell == null && !grid.isUpperRow(majorBallCell)) {
            return null;
        }
        if (!majorBallCell.isEmpty()) {
            majorBallCell = flyingMajorBall.flashBackToFirstCell(grid.r() * 0.1f, grid, true);
        }
        majorBallCell.add(flyingMajorBall);
        flyingMajorBall.setScales(1f);
        return majorBallCell;
    }

    private boolean updateFireMajorBall() {
        Cell collisionBallCell = grid.getCollisionBallCell(flyingMajorBall, getGridBallSize() * Settings.COLLISION_PERCENT);
        if (collisionBallCell != null) {
            Ball ball = (Ball) collisionBallCell.remove();
            ball.delete(Rnd.nextFloat(0f, 0.1f));
            deletingCount++;
            game.getSounds().play(EXPLOSION_FIREBALL);
        }
        if (grid.isMe(flyingMajorBall.getPos())) {
            return false;
        } else {
            poolBalls.free(flyingMajorBall);
            return true;
        }
    }

    private void updateBtnFireballStatus() {
        float progress = btnFireball.getProgress();
        if (progress == 1f) return;
        if (deletingCount != 0 || fallDownCount != 0) {
            progress += 0.15f;
            if (progress > 1f) progress = 1f;
            btnFireball.setProgress(progress);
        } else {
            btnFireball.setProgress(0f);
        }
    }

    private void updateBtnBombBallStatus() {
        float progress = btnBomb.getProgress();
        if (progress == 1f) return;
        if (deletingCount != 0 || fallDownCount != 0) {
            progress += 0.2f;
            if (progress > 1f) progress = 1f;
            btnBomb.setProgress(progress);
        } else {
            btnBomb.setProgress(0f);
        }
    }

    @Override
    public void onSpinnerCompleteRotation(Spinner spinner) {
        switch (state) {
            case ROTATION_SPINNER:
                setState(State.WAITING);
                pathTracker.engageIfArm();
                ballsCartridgeComplete(spinner);
                break;
            case FLYING_MAJOR_AND_ROTATION_SPINNER:
                setState(State.FLYING_MAJOR_BALL);
                ballsCartridgeComplete(spinner);
                break;
            case COMPLETE_LEVEL:
            case GAME_OVER:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public void onSpinnerChangePosition(Spinner spinner) {
        for (int i = 0; i < cartridge.length; i++) {
            if (cartridge[i] != null) {
                cartridge[i].setScales(CARTRIDGE_BALLS_SIZE_PERCENT);
                cartridge[i].setPos(spinner.getPosition(i));
            }
        }
    }

    private void ballsCartridgeComplete(Spinner spinner) {
        int prevIndex = spinner.getPrevIndex();
        if (cartridge[prevIndex] == null) {
            cartridge[prevIndex] = getRandomBall();
            cartridge[prevIndex].setToCartridge();
            cartridge[prevIndex].setScales(CARTRIDGE_BALLS_SIZE_PERCENT);
            cartridge[prevIndex].setPos(spinner.getPosition(prevIndex));
        }
    }

    public void resetCartridgeAndFillRandomBalls() {
        for (int i = 0; i < cartridge.length; i++) {
            if (cartridge[i] != null) poolBalls.free(cartridge[i]);
            cartridge[i] = getRandomBall();
            cartridge[i].setToCartridge();
            cartridge[i].setScales(CARTRIDGE_BALLS_SIZE_PERCENT);
        }
        spinner.setIdle(0);
    }

    Ball[] getCartridge() {
        return cartridge;
    }

    private boolean isShowWarningLine() {
        int f = grid.findLowerNotEmptyRow();
        if (f == -1) {
            return false;
        } else {
            return grid.getRowBottom(f) < spinner.getTop() + 0.15f;
        }
    }

    private boolean isGameOver() {
        int f = grid.findLowerNotEmptyRow();
        return f != -1 && grid.getRowBottom(f) < disengagementBorder;
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == btnFireball) {
            cartridge[spinner.getUpperIndex()].setType(Ball.Type.FIRE);
        } else if (src == btnBomb) {
            cartridge[spinner.getUpperIndex()].setType(Ball.Type.BOMB);
        } else if (src == btnSettings) {
            state = State.SETTINGS;
        } else if (src == btnBack) {
            processBack();
        }
    }

    @Override
    public void onDialogGameOverMenu() {
        setBackScreen();
    }

    @Override
    public void onDialogSettingsChangeMusic(boolean isMusicOn) {
        game.setMusicOn(isMusicOn);
    }

    @Override
    public void onDialogSettingsChangeSound(boolean isSoundOn) {
        game.setSoundOn(isSoundOn);
    }

    @Override
    public void onDialogSettingsClose() {
        state = State.WAITING;
    }

    private static final float BACK_INTERVAL = 0.5f;
    private boolean backPressed;
    private float backTimer;

    private void updateBackTimer(float deltaTime) {
        if (!backPressed) return;
        backTimer += deltaTime;
        if (backTimer >= BACK_INTERVAL) {
            backTimer = 0f;
            backPressed = false;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if (game.isAndroid()) {
                if (backPressed) {
                    processBack();
                } else {
                    backPressed = true;
                    game.toastLong("Double press \"back\" to exit.");
                }
            } else {
                processBack();
            }
        }
        return false;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void processBack() {
        switch (state) {
            case SETTINGS:
                state = State.WAITING;
                break;
            default:
                setBackScreen();
        }
    }

    private void setBackScreen() {
        game.setScreen(new SelectLevelsScreen(game));
    }

    @Override
    public BallsGrid getGrid() {
        return grid;
    }

    private float getGridBallSize() {
        return grid.r() * 2f * Settings.BALL_SIZE_PERCENT;
    }

    @Override
    public void createNewGrid(int m, int n, boolean isShortBase) {
        if (grid != null) {
            grid.downfallAllBall(Settings.DOWNFALL_V0, Settings.DOWNFALL_A);
        }
        grid = new BallsGrid(m, n, isShortBase);
        grid.setWidthProportion(GRID_WIDTH);
        gridView = new GridView(grid);
        poolBalls.setBallSize(getGridBallSize());
        pathTracker.setLeftWall(grid.getLeft() + getGridBallSize() * 0.5f);
        pathTracker.setRightWall(grid.getRight() - getGridBallSize() * 0.5f);
        pathTracker.setInterval(getGridBallSize() * 0.5f);
        pathTracker.setPrecision(10);
        pathTracker.setTrackerHeight(0.0084f);
        System.gc();
    }

    private void draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        Drawer.draw(batch, poolBalls.getActiveObjects());
        drawBallEffects();
        spinner.draw(batch);
        pathTracker.draw(batch);
        if (isShowWarningLine()) warningLine.draw(batch);
        leftBorder.draw(batch);
        rightBorder.draw(batch);
        drawOther();
        btnBack.draw(batch);
        btnSettings.draw(batch);
        btnFireball.draw(batch);
        btnBomb.draw(batch);
        switch (state) {
            case GAME_OVER:
                dialogGameOver.draw(batch);
                break;
            case COMPLETE_LEVEL:
                drawNextLevel();
                break;
            case SETTINGS:
                dialogSettings.draw(batch);
        }
        batch.end();
        if (DRAW_HEX_GRID) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            gridView.drawHexGrid(shapeRenderer);
            if (DRAW_LINES) gridView.drawLines(shapeRenderer);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(minPortraitWorldBounds.getLeft(), minPortraitWorldBounds.getBottom(), minPortraitWorldBounds.getWidth(), minPortraitWorldBounds.getHeight());
            shapeRenderer.rect(maxPortraitWorldBounds.getLeft(), maxPortraitWorldBounds.getBottom(), maxPortraitWorldBounds.getWidth(), maxPortraitWorldBounds.getHeight());
            shapeRenderer.end();
        }
    }

    private void drawBallEffects() {
        Drawer.draw(batch, poolBalls.getEffectBallExplosionPool().getActiveObjects());
        Drawer.draw(batch, poolBalls.getDebugMarkerBallDeletedPool().getActiveObjects());
        Drawer.draw(batch, poolBalls.getDebugMarkersBallDownfallPool().getActiveObjects());
    }

    protected void drawOther() {
    }

    protected void drawNextLevel() {
    }

    @SuppressWarnings("unused")
    @Override
    public void hide() {
        save();
        if (DRAW_HEX_GRID) shapeRenderer.dispose();
        game.getPoolBalls().freeAllActiveObjects();
    }

    @SuppressWarnings("unused")
    @Override
    public void pause() {
        save();
    }

    @Override
    public boolean isPathTrackerBallCollision(Vector2 pos) {
        Cell cell = grid.getCell(pos);
        return !cell.isEmpty() || cell.m() == grid.m() - 1;
    }

    @Override
    public boolean isPathTrackerOutOfGrid(Vector2 pos) {
        return !grid.isMe(pos);
    }

    public Ball getFlyingMajorBall() {
        return flyingMajorBall;
    }

    void setFlyingMajorBallFromSave(Ball flyingMajorBall) {
        this.flyingMajorBall = flyingMajorBall;
    }

    void setCartridgeFromSave(Ball[] balls) {
        System.arraycopy(balls, 0, cartridge, 0, cartridge.length);
        for (int i = 0; i < cartridge.length; i++) {
            if (cartridge[i] != null) {
                cartridge[i].setToCartridge();
                cartridge[i].setScales(CARTRIDGE_BALLS_SIZE_PERCENT);
            }
        }
    }

    public void setFireballProgress(float progress) {
        btnFireball.setProgress(progress);
    }

    public void setBombProgress(float progress) {
        btnBomb.setProgress(progress);
    }

    float getFireballProgress() {
        return btnFireball.getProgress();
    }

    float getBombProgress() {
        return btnBomb.getProgress();
    }

    Spinner getSpinner() {
        return spinner;
    }

    PoolBalls getPoolBalls() {
        return poolBalls;
    }

    abstract protected Ball getRandomBall();

    abstract protected void updateCountersAndGridPosition(float deltaTime, boolean isFlyingMajor);

    protected void afterHandleMajorBall() {
        updateBtnFireballStatus();
        updateBtnBombBallStatus();
    }

    abstract protected void save();

    abstract protected boolean isCompleteLevel();

    protected void updateCompleteLevel(float deltaTime) {
    }

    protected void onCompleteLevel() {
    }
}