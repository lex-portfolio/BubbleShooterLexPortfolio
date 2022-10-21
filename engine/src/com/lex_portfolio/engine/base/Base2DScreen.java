package com.lex_portfolio.engine.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.math.MatrixUtils;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.math.world_bounds.MaxLandscapeBounds;
import com.lex_portfolio.engine.math.world_bounds.MaxPortraitBounds;
import com.lex_portfolio.engine.math.world_bounds.MinLandscapeBounds;
import com.lex_portfolio.engine.math.world_bounds.MinPortraitBounds;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Base2DScreen<T extends BaseGame> implements InputProcessor {

    protected final TypeSize typeSize;
    protected final float size;
    protected final T game;

    protected final Rect screenBounds = new Rect();
    protected final Rect worldBounds = new Rect();
    protected final MinPortraitBounds minPortraitWorldBounds = new MinPortraitBounds();
    protected final MaxPortraitBounds maxPortraitWorldBounds = new MaxPortraitBounds();
    protected final MinLandscapeBounds minLandscapeBounds = new MinLandscapeBounds();
    protected final MaxLandscapeBounds maxLandscapeBounds = new MaxLandscapeBounds();
    protected final Rect glBounds = new Rect(0f, 0f, 1f, 1f);

    protected final Matrix4 matWorldToGl = new Matrix4();
    protected final Matrix3 matScreenToWorld = new Matrix3();

    protected final SpriteBatch batch;

    public Base2DScreen(T game, float size, TypeSize typeSize) {
        this.game = game;
        this.size = size;
        this.typeSize = typeSize;
        batch = game.getBatch();
        switch (typeSize) {
            case WIDTH:
                minPortraitWorldBounds.setBoundsWidth(size);
                maxPortraitWorldBounds.setBoundsWidth(size);
                minLandscapeBounds.setBoundsWidth(size);
                maxLandscapeBounds.setBoundsWidth(size);
                break;
            case HEIGHT:
                minPortraitWorldBounds.setBoundsHeight(size);
                maxPortraitWorldBounds.setBoundsHeight(size);
                minLandscapeBounds.setBoundsHeight(size);
                maxLandscapeBounds.setBoundsHeight(size);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeSize);
        }
    }

    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    public final void resize(int width, int height) {
        screenBounds.setSize(width, height);
        screenBounds.setLeft(0f);
        screenBounds.setBottom(0f);
        float aspect = screenBounds.getAspect();
        switch (typeSize) {
            case WIDTH:
                worldBounds.setWidthProportion(size, aspect);
                break;
            case HEIGHT:
                worldBounds.setHeightProportion(size, aspect);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeSize);
        }
        MatrixUtils.calcTransitionMatrix(matWorldToGl, worldBounds, glBounds);
        updateProjectMatrix();
        MatrixUtils.calcTransitionMatrix(matScreenToWorld, screenBounds, worldBounds);
        resize(worldBounds);
    }

    public void updateProjectMatrix() {
        batch.setProjectionMatrix(matWorldToGl);
    }

    protected void resize(Rect worldBounds) {
    }

    public void hide() {
    }

    public void pause() {
    }

    @SuppressWarnings("EmptyMethod")
    public void resume() {
    }

    public void render(float deltaTime) {
    }

    public T getGame() {
        return game;
    }

    @SuppressWarnings("EmptyMethod")
    protected void multiTouchDown(Vector2 touch, long eventTime, int pointer) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void multiTouchMove(Vector2 touch, long eventTime, int pointer) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void multiTouchUp(Vector2 touch, long eventTime, int pointer) {
    }

    protected void touchDown(Vector2 touch, long eventTime) {
    }

    protected void touchMove(Vector2 touch, long eventTime) {
    }

    protected void touchUp(Vector2 touch, long eventTime) {
    }


    private final Vector2 touch = new Vector2();
    private boolean pressed;
    private int pointer;

    @Override
    public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, Gdx.graphics.getHeight() - screenY - 1).mul(matScreenToWorld);
        long eventTime = Gdx.input.getCurrentEventTime();
        multiTouchDown(touch, eventTime, pointer);
        if (!pressed) {
            pressed = true;
            this.pointer = pointer;
            touchDown(touch, eventTime);
        }
        return false;
    }

    @Override
    public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, Gdx.graphics.getHeight() - screenY - 1).mul(matScreenToWorld);
        long eventTime = Gdx.input.getCurrentEventTime();
        multiTouchUp(touch, eventTime, pointer);
        if (pressed && this.pointer == pointer) {
            pressed = false;
            touchUp(touch, eventTime);
        }
        return false;
    }

    @Override
    public final boolean touchDragged(int screenX, int screenY, int pointer) {
        touch.set(screenX, Gdx.graphics.getHeight() - screenY - 1).mul(matScreenToWorld);
        long eventTime = Gdx.input.getCurrentEventTime();
        multiTouchMove(touch, eventTime, pointer);
        if (pressed && this.pointer == pointer) {
            touchMove(touch, eventTime);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public final boolean keyTyped(char character) {
        return false;
    }

    @Override
    public final boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}