package com.lex_portfolio.bubble_shooter.level_editor.screens.main_screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.BallsGrid;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.bubble_shooter.level_editor.LevelEditor;
import com.lex_portfolio.bubble_shooter.level_editor.SelectBallTypePanel;
import com.lex_portfolio.bubble_shooter.screens.common.BallsGridScreen;
import com.lex_portfolio.bubble_shooter.screens.common.GameScreen;
import com.lex_portfolio.bubble_shooter.settings.Settings;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.base.TypeSize;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.hex_grid.GridView;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;
import com.lex_portfolio.engine.ui.panels.SelectPanelListener;
import com.lex_portfolio.engine.utils.StrBuilder;
import com.lex_portfolio.engine.view.Drawer;

public class LevelEditorScreen extends Base2DScreen<LevelEditor> implements SelectPanelListener, ActionListener, BallsGridScreen {

    private static final float WORLD_HEIGHT = 1f;

    private ShapeRenderer shapeRenderer;
    private BallsGrid grid;
    private GridView gridView;

    private enum Instrument {BALL_BRUSH, ERASE_BRUSH}

    private Instrument instrument = Instrument.BALL_BRUSH;

    private PoolBalls pollBalls;
    private SelectBallTypePanel selectBallTypePanel;
    private Font font;
    private ScaledTouchUpButton btnEraseBrush;
    private ScaledTouchUpButton btnRegenerate;
    private ScaledTouchUpButton btnNext;
    private ScaledTouchUpButton btnBack;
    private int stage;
    private int cursorTypeIndex;
    private MainScreenLoader loader;
    private LevelGenerator levelGenerator;

    public LevelEditorScreen(LevelEditor game) {
        super(game, WORLD_HEIGHT, TypeSize.HEIGHT);
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        font = game.getFont("font.fnt");
        font.setWorldSize(0.02f);
        BaseTextureAtlas atlas = game.getAtlas("level_editor.atlas");

        pollBalls = new PoolBalls(atlas);

        selectBallTypePanel = new SelectBallTypePanel(this, atlas, pollBalls);

        btnEraseBrush = new ScaledTouchUpButton(atlas.getRegion("btn_del"), this);
        btnEraseBrush.setHeightProportion(0.05f);

        btnRegenerate = new ScaledTouchUpButton(atlas.getRegion("btn_regenerate"), this);
        btnRegenerate.setHeightProportion(0.05f);

        btnNext = new ScaledTouchUpButton(atlas.getRegion("btn_next"), this);
        btnNext.setHeightProportion(0.05f);

        btnBack = new ScaledTouchUpButton(atlas.getRegion("btn_back"), this);
        btnBack.setHeightProportion(0.05f);

        shapeRenderer = new ShapeRenderer();
        levelGenerator = new LevelGenerator(pollBalls, grid);
        loader = new MainScreenLoader(this);
        loader.load(0);
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        super.resize(worldBounds);
        grid.setBottom(worldBounds.getBottom());
        shapeRenderer.setProjectionMatrix(matWorldToGl);
        selectBallTypePanel.resize(worldBounds);

        btnEraseBrush.setLeft(worldBounds.getLeft());
        btnEraseBrush.setTop(worldBounds.getTop() - 0.05f);

        btnRegenerate.setLeft(worldBounds.getLeft());
        btnRegenerate.setTop(worldBounds.getTop() - 0.10f);

        btnBack.setLeft(worldBounds.getLeft());
        btnBack.setTop(worldBounds.getTop() - 0.20f);

        btnNext.setLeft(worldBounds.getLeft());
        btnNext.setTop(worldBounds.getTop() - 0.28f);
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == btnEraseBrush) {
            instrument = Instrument.ERASE_BRUSH;
        } else if (src == btnNext) {
            loader.save(this).load(++stage);
        } else if (src == btnBack) {
            if (stage != 0) loader.save(this).load(--stage);
        } else if (src == btnRegenerate) {
            levelGenerator.generateLevel(stage);
        } else {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        draw();
    }

    private void update(float deltaTime) {
        selectBallTypePanel.update(deltaTime);
        pollBalls.updateAndFreeAllDestroyed(deltaTime, grid);
    }

    private final StrBuilder sb = new StrBuilder();

    private void draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        Drawer.draw(batch, pollBalls.getActiveObjects());
        selectBallTypePanel.draw(batch);
        btnEraseBrush.draw(batch);
        btnRegenerate.draw(batch);
        btnNext.draw(batch);
        btnBack.draw(batch);
        font.draw(batch, sb.clean().append(stage), worldBounds.getLeft(), worldBounds.getTop() - 0.25f, Align.left);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        gridView.drawHexGrid(shapeRenderer);
        shapeRenderer.end();
    }

    private boolean gridDragged;
    private float dy;

    @SuppressWarnings("unused")
    @Override
    protected void touchDown(Vector2 touch, long eventTime) {
        if (btnEraseBrush.touchDown(touch)) return;
        if (btnRegenerate.touchDown(touch)) return;
        if (btnNext.touchDown(touch)) return;
        if (btnBack.touchDown(touch)) return;
        if (selectBallTypePanel.touchDown(touch)) return;
        if (grid.isMe(touch)) {
            processGridEdit(touch);
        } else {
            gridDragged = true;
            dy = grid.getPos().y - touch.y;
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchMove(Vector2 touch, long eventTime) {
        if (gridDragged) {
            grid.setPosY(touch.y + dy);
            return;
        }
        if (grid.isMe(touch)) processGridEdit(touch);
    }

    private void processGridEdit(Vector2 touch) {
        Cell cell = grid.getCell(touch);
        if (!cell.isEmpty()) pollBalls.free((Ball) cell.remove());
        switch (instrument) {
            case BALL_BRUSH:
                Ball ball = pollBalls.obtain(Ball.Type.values()[cursorTypeIndex]);
                cell.add(ball);
                ball.setScales(1f);
                break;
            case ERASE_BRUSH:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + instrument);
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchUp(Vector2 touch, long eventTime) {
        gridDragged = false;
        if (btnEraseBrush.touchUp(touch)) return;
        if (btnRegenerate.touchUp(touch)) return;
        if (btnNext.touchUp(touch)) return;
        if (btnBack.touchUp(touch)) return;
        selectBallTypePanel.touchUp(touch);
    }

    @SuppressWarnings("unused")
    @Override
    public void onSelect(Object src, Object btn, int index) {
        if (src == selectBallTypePanel) {
            if (index < Ball.Type.getMaxGridableCount()) {
                instrument = Instrument.BALL_BRUSH;
                cursorTypeIndex = index;
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void pause() {
        loader.save(this);
    }

    @SuppressWarnings("unused")
    @Override
    public void hide() {
        loader.save(this);
        pollBalls.dispose();
        font.dispose();
        shapeRenderer.dispose();
        super.hide();
    }

    void setStage(int stage) {
        this.stage = stage;
    }

    @Override
    public BallsGrid getGrid() {
        return grid;
    }

    @Override
    public void createNewGrid(int m, int n, boolean isShortBase) {
        if (grid != null) {
            grid.downfallAllBall(Settings.DOWNFALL_V0, Settings.DOWNFALL_A);
        }
        grid = new BallsGrid(m, n, isShortBase);
        grid.setWidthProportion(GameScreen.GRID_WIDTH);
        grid.setBottom(worldBounds.getBottom());
        gridView = new GridView(grid);
        pollBalls.setBallSize(grid.r() * 2f);
    }

    int getStage() {
        return stage;
    }

    PoolBalls getPoolBalls() {
        return pollBalls;
    }
}