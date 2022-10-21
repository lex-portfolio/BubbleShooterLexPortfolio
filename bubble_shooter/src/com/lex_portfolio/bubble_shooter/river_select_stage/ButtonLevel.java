package com.lex_portfolio.bubble_shooter.river_select_stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.bubble_shooter.statistics.Statistics;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.utils.StrBuilder;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class ButtonLevel extends Sprite {

    enum Type {LEFT, RIGHT}

    private final Type type;
    private final StrBuilder sb = new StrBuilder();
    private final Font font;
    private final SelectStageListener listener;
    private final Sprite[] stars;
    private final Sprite marker;
    private final Statistics statistics;
    private boolean enabled;
    private int stage;

    private float fontSize;
    private float fontDx;

    ButtonLevel(
            TextureRegion[] regions,
            TextureRegion[] regionStars,
            TextureRegion regionMarker,
            Font font,
            Statistics statistics,
            Type type,
            SelectStageListener listener
    ) {
        super(regions);
        this.font = font;
        this.statistics = statistics;
        this.type = type;
        this.listener = listener;
        stars = new Sprite[4];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Sprite(regionStars[i]);
        }
        marker = new Sprite(regionMarker);
    }

    void setHeightAllProportions(float buttonHeight, float markerHeight, float starsHeight) {
        setHeightProportion(buttonHeight);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setHeightProportion(starsHeight);
        }
        marker.setHeightProportion(markerHeight);
        fontDx = 0.06f * getHalfWidth();
        if (type == Type.RIGHT) fontDx = -fontDx;
    }

    private boolean pressed;

    boolean touchDown(Vector2 touch) {
        if (!enabled || !isMe(touch)) return false;
        pressed = true;
        setScales(0.9f);
        return true;
    }

    void touchUp(Vector2 touch) {
        if (!enabled || !pressed) return;
        pressed = false;
        setScales(1f);
        if (isMe(touch)) listener.onSelectStage(stage);
    }

    public void update(int stage, Vector2 bottomPos) {
        this.stage = stage;
        pos.x = bottomPos.x;
        setBottom(bottomPos.y);
        this.enabled = stage <= statistics.getLastAvailableStage();
        frame = enabled ? 1 : 0;
        if (stage >= 0 && stage < 10) {
            fontSize = 0.13f * getHeight();
        } else if (stage >= 10 && stage < 100) {
            fontSize = 0.10f * getHeight();
        } else {
            fontSize = 0.07f * getHeight();
        }
        float markerDx = 0.1f * halfWidth;
        final float markerDy = halfHeight;
        float starsDx = 0f * halfWidth;
        float startsDy = 0.85f * halfHeight;
        int starsCount = statistics.getStars(stage);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setScales(0f);
        }
        switch (type) {
            case LEFT:
                markerDx = -markerDx;
                stars[starsCount].getPos().set(starsDx, startsDy).add(pos);
                stars[starsCount].setScales(1f);
                break;
            case RIGHT:
                stars[starsCount].getPos().set(-starsDx, startsDy).add(pos);
                stars[starsCount].setScales(1f);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        marker.getPos().set(markerDx, markerDy).add(pos);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        if (enabled) {
            font.setWorldSize(fontSize * getScales());
            font.draw(batch, sb.clean().append(stage), pos.x + fontDx, pos.y + font.getCapHeight() * 0.5f, Align.center);
            if (stage == statistics.getLastAvailableStage()) {
                marker.draw(batch);
            } else {
                for (int i = 0; i < stars.length; i++) {
                    stars[i].draw(batch);
                }
            }
        }
    }
}
