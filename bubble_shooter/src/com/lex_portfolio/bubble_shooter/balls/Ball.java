package com.lex_portfolio.bubble_shooter.balls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.game_elements.explosions.Explosion;
import com.lex_portfolio.engine.game_elements.explosions.ExplosionPool;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.CellObject;
import com.lex_portfolio.engine.math.grid.hex_grid.NonRegularHexGrid;
import com.lex_portfolio.engine.sprites.Sprite;

@SuppressWarnings("SpellCheckingInspection")
public class Ball extends Sprite implements CellObject {

    //DEBUG
    private static final boolean DEBUG = false;
    private static final float FREEZE_DELETED_INTERVAL = 4f;
    private static final float FREEZE_FALL_DOWN_INTERVAL = 8f;
    private final DebugMarkersPool debugMarkerBallDeletedPool;
    private final DebugMarkersPool debugMarkersBallDownfallPool;
    private float freezeDeletedTimer;
    private float freezeDownfallTimer;

    //RELEASE
    @SuppressWarnings("SpellCheckingInspection")
    public enum Type {
        RED, LIME, BLUE, YELLOW, CYAN, WHITE, MAGENTA, ORANGE, PURPLE, IRON, BOMB, FIRE;

        public boolean isColored() {
            return this.ordinal() < IRON.ordinal();
        }

        public static int getMaxColorCount() {
            return PURPLE.ordinal() + 1;
        }

        public static int getMaxGridableCount() {
            return IRON.ordinal() + 1;
        }
    }

    private enum State {IN_CELL, IN_CARTRIDGE, MAJOR_FLYING, DELETING, FALL_DOWNING}

    private State state;
    private Type type;
    private final Vector2 v = new Vector2();
    private final Vector2 a = new Vector2();
    private float deleteTimer;

    private final ExplosionPool explosion1Pool;

    Ball(TextureRegion[] regions, ExplosionPool explosion1Pool, DebugMarkersPool debugMarkerBallDeletedPool, DebugMarkersPool debugMarkersBallDownfallPool) {
        super(regions);
        this.explosion1Pool = explosion1Pool;
        this.debugMarkerBallDeletedPool = debugMarkerBallDeletedPool;
        this.debugMarkersBallDownfallPool = debugMarkersBallDownfallPool;
    }

    public void setType(Type type) {
        this.type = type;
        frame = type.ordinal();
    }

    public Type getType() {
        return type;
    }

    void update(float deltaTime, Rect gridBounds) {
        switch (state) {
            case IN_CELL:
                if (getScales() < 1f) {
                    addScale(deltaTime);
                    if (getScales() > 1f) setScales(1f);
                }
                break;
            case IN_CARTRIDGE:
                if (type == Type.BOMB || type == Type.FIRE) angle += -180f * deltaTime;
                break;
            case MAJOR_FLYING:
                updateMajorFlying(deltaTime, gridBounds);
                break;
            case DELETING:
                if (DEBUG) {
                    freezeDeletedTimer -= deltaTime;
                    if (freezeDeletedTimer <= 0f) destroy();
                } else {
                    deleteTimer -= deltaTime;
                    if (deleteTimer <= 0f) {
                        Explosion explosion = explosion1Pool.obtain();
                        explosion.start(getPos(), getHeight(), 0.5f, 1f);
                        destroy();
                    }
                }
                break;
            case FALL_DOWNING:
                if (DEBUG) {
                    freezeDownfallTimer -= deltaTime;
                    if (freezeDownfallTimer <= 0f) updateFallDown(deltaTime, gridBounds);
                } else {
                    updateFallDown(deltaTime, gridBounds);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void updateMajorFlying(float deltaTime, Rect gridBounds) {
        pos.mulAdd(v, deltaTime);
        if (type == Type.FIRE) {
            angle += -360 * deltaTime;
        } else if (type == Type.IRON || type == Type.BOMB || type.isColored()) {
            if (type == Type.BOMB) {
                angle += -360 * deltaTime;
            }
            if (getLeft() < gridBounds.getLeft()) {
                setLeft(gridBounds.getLeft());
                v.x = -v.x;
            }
            if (getRight() > gridBounds.getRight()) {
                setRight(gridBounds.getRight());
                v.x = -v.x;
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void updateFallDown(float deltaTime, Rect gridBounds) {
        v.mulAdd(a, deltaTime);
        angle = v.angleDeg();
        pos.mulAdd(v, deltaTime);
        if (getLeft() < gridBounds.getLeft()) {
            setLeft(gridBounds.getLeft());
            v.x = -v.x;
        }
        if (getRight() > gridBounds.getRight()) {
            setRight(gridBounds.getRight());
            v.x = -v.x;
        }
        if (getTop() < gridBounds.getBottom()) destroy();
    }

    public void setToCartridge() {
        state = State.IN_CARTRIDGE;
    }

    public void majorFlying(Vector2 v) {
        state = State.MAJOR_FLYING;
        setScales(1f);
        this.v.set(v);
    }

    void downfall(float vx, float ay, float angle) {
        v.set(vx, 0f).rotateDeg(angle);
        a.set(0f, ay);
        setScales(1f);
        state = State.FALL_DOWNING;
        if (DEBUG) {
            freezeDownfallTimer = FREEZE_FALL_DOWN_INTERVAL;
            DebugMarker arrow = debugMarkersBallDownfallPool.obtain();
            arrow.start(getPos(), getHeight() * 0.2f, angle, FREEZE_FALL_DOWN_INTERVAL + 2f);
        }
    }

    public void delete(float delay) {
        state = State.DELETING;
        deleteTimer = delay;
        setScales(1f);
        if (DEBUG) {
            freezeDeletedTimer = FREEZE_DELETED_INTERVAL;
            DebugMarker cross = debugMarkerBallDeletedPool.obtain();
            cross.start(pos, getHeight() * 0.5f, 0f, FREEZE_DELETED_INTERVAL);
        }
    }

    private final Vector2 backV = new Vector2();

    public Cell flashBackToFirstCell(float ds, NonRegularHexGrid grid, boolean isNeedEmptyCell) {
        backV.set(v);
        backV.scl(-1f, -1f).nor().scl(ds);
        while (true) {
            pos.add(backV);
            if (getLeft() < grid.getLeft()) {
                setLeft(grid.getLeft());
                backV.x = -backV.x;
            }
            if (getRight() > grid.getRight()) {
                setRight(grid.getRight());
                backV.x = -backV.x;
            }
            if (getBottom() < grid.getBottom())
                throw new RuntimeException("Не найдена свободная ячейка");
            Cell cell = grid.getCell(pos);
            if (cell != null) {
                if (isNeedEmptyCell) {
                    if (cell.isEmpty()) return cell;
                } else {
                    return cell;
                }
            }
        }
    }

    @Override
    public void onAddedToCell(Cell cell) {
        pos.set(cell.getPos());
        state = State.IN_CELL;
        setScales(0f);
    }

    @Override
    public void onChangeCell(Cell cell) {
        pos.set(cell.getPos());
    }

    @SuppressWarnings("unused")
    @Override
    public void onRemovedFromCell(Cell cell) {
    }

    @Override
    public void onObtainFromPool() {
        super.onObtainFromPool();
        v.setZero();
        setScales(1f);
        angle = 0f;
    }

    public boolean inCell() {
        return state == State.IN_CELL;
    }

    public State getState() {
        return state;
    }

    public String getStringV() {
        return "v.x=" + v.x + " v.y=" + v.y;
    }

    public String getStringPos() {
        return "pos.x=" + pos.x + " pos.y=" + pos.y;
    }

    public void setMajorFromSave(float posx, float posy, float vx, float vy) {
        state = State.MAJOR_FLYING;
        pos.set(posx, posy);
        v.set(vx, vy);
    }
}
