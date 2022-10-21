package com.lex_portfolio.engine.ui.river;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.touches.kinetic_touches.KineticTouches;
import com.lex_portfolio.engine.ui.touches.kinetic_touches.KineticTouchesListener;

public class RiverVertical implements KineticTouchesListener {

    private final KineticTouches kineticTouches;
    private Sprite top;
    private Sprite bottom;
    private int page;
    private final int lastPage;

    public RiverVertical(TextureRegion region, int lastPage) {
        this.lastPage = lastPage;
        top = new Sprite(region);
        bottom = new Sprite(region);
        kineticTouches = new KineticTouches(this);
    }

    private float worldBottom;
    private float overlap;


    @SuppressWarnings("unused")
    public void resize(Rect worldBounds) {
        worldBottom = worldBounds.getBottom();
        float pageWidth = worldBounds.getWidth();
        bottom.setWidthProportion(pageWidth);
        top.setWidthProportion(pageWidth);
        overlap = bottom.getHeight() * 0.002f;
    }

    protected Sprite getPageBgSprite() {
        return bottom;
    }

    private float dy;
    private float vy;

    @SuppressWarnings("unused")
    public void touchDown(Vector2 touch, long eventTime) {
        kineticTouches.touchDown(touch, eventTime);
    }

    @SuppressWarnings("unused")
    public void touchMove(Vector2 touch, long eventTime) {
        kineticTouches.touchMove(touch, eventTime);
    }

    @SuppressWarnings("unused")
    public void touchUp(Vector2 touch, long eventTime) {
        kineticTouches.touchUp(touch, eventTime);
    }

    private boolean listToPage;

    @SuppressWarnings("unused")
    public void update(float deltaTime) {
        kineticTouches.update(deltaTime);
        if (listToPage) {
            listToPage = false;
            kineticTouches.stop();
            bottom.setBottom(worldBottom);
            top.setBottom(bottom.getTop() - overlap);
            return;
        }
        bottom.getPos().y += vy * deltaTime;
        top.setBottom(bottom.getTop() - overlap);

        if (bottom.getTop() < worldBottom) {
            if (page == lastPage) {
                kineticTouches.stop();
                bottom.setTop(worldBottom);
                top.setBottom(bottom.getTop());
            } else {
                Sprite sprite = top;
                top = bottom;
                bottom = sprite;
                top.setBottom(bottom.getTop());
                page++;
//                Gdx.app.log("UP_PAGE:", "new page = " + page);
            }
        } else if (bottom.getBottom() > worldBottom) {
            if (page == 0) {
                kineticTouches.stop();
                bottom.setBottom(worldBottom);
                top.setBottom(bottom.getTop());
            } else {
                Sprite sprite = top;
                top = bottom;
                bottom = sprite;
                bottom.setTop(top.getBottom());
                page--;
//                Gdx.app.log("DOWN_PAGE:", "new page = " + page);
            }
        }
    }

    public int getPage() {
        return page;
    }

    public void listToPage(int page) {
        if (page < 0 || page > lastPage) throw new IllegalArgumentException("page: " + page);
        this.page = page;
        listToPage = true;
    }

    public float getBottomPosY() {
        return bottom.getPos().y;
    }

    public void setFromSave(int page, float bottomPosY) {
        this.page = page;
        bottom.getPos().y = bottomPosY;
    }

    @SuppressWarnings("unused")
    public void draw(SpriteBatch batch) {
        bottom.draw(batch);
        top.draw(batch);
    }

    @SuppressWarnings("unused")
    @Override
    public void onKineticBypassClick(Vector2 touch) {
    }

    @Override
    public void onKineticFirstMove(Vector2 touch) {
        dy = bottom.getPos().y - touch.y;
    }

    @Override
    public void onKineticFollowingMoves(Vector2 touch) {
        bottom.getPos().y = touch.y + dy;
        top.setBottom(bottom.getTop() - overlap);

        if (bottom.getTop() < worldBottom) {
            if (page == lastPage) {
                kineticTouches.stop();
                bottom.setTop(worldBottom);
                top.setBottom(bottom.getTop());
            } else {
                Sprite sprite = top;
                top = bottom;
                bottom = sprite;
                top.setBottom(bottom.getTop());
                dy = bottom.getPos().y - kineticTouches.getLastTouch().getPos().y;
                page++;
//                Gdx.app.log("UP_PAGE:", "new page = " + page);
            }
        } else if (bottom.getBottom() > worldBottom) {
            if (page == 0) {
                kineticTouches.stop();
                bottom.setBottom(worldBottom);
                top.setBottom(bottom.getTop());
            } else {
                Sprite sprite = top;
                top = bottom;
                bottom = sprite;
                bottom.setTop(top.getBottom());
                dy = bottom.getPos().y - kineticTouches.getLastTouch().getPos().y;
                page--;
//                Gdx.app.log("DOWN_PAGE:", "new page = " + page);
            }
        }
    }

    @Override
    public void onKineticChangeVelocity(Vector2 v) {
        vy = v.y * 2f;
    }
}