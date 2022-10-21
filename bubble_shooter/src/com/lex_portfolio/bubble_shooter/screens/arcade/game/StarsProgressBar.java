package com.lex_portfolio.bubble_shooter.screens.arcade.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.progress_bars.ProgressBar;

@SuppressWarnings("ForLoopReplaceableByForEach")
class StarsProgressBar extends ProgressBar {

    private final Sprite[] stars = new Sprite[3];

    StarsProgressBar(TextureRegion regionLine, TextureRegion[] starsRegions) {
        super(regionLine, Type.HORIZONTAL);
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Sprite(starsRegions);
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setHeightProportion(2.2f * height);
        }
    }

    int getsStarsCount() {
        if (progress < 0.25) return 0;
        if (progress < 0.50) return 1;
        if (progress < 0.75) return 2;
        return 3;
    }

    @SuppressWarnings("unused")
    void setProgress(float progress) {
        if (progress < 0f || progress > 1f) throw new IllegalArgumentException();
        this.progress = progress;
        int starsCount = getsStarsCount();
        switch (starsCount) {
            case 0:
                stars[0].setFrame(1);
                stars[1].setFrame(1);
                stars[2].setFrame(1);
                break;
            case 1:
                stars[0].setFrame(0);
                stars[1].setFrame(1);
                stars[2].setFrame(1);
                break;
            case 2:
                stars[0].setFrame(0);
                stars[1].setFrame(0);
                stars[2].setFrame(1);
                break;
            case 3:
                stars[0].setFrame(0);
                stars[1].setFrame(0);
                stars[2].setFrame(0);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + starsCount);
        }
    }

    @Override
    public void setPos(float x, float y) {
        super.setPos(x, y);
        float dx = getWidth() / 4;
        for (int i = 0; i < stars.length; i++) {
            stars[i].getPos().x = getLeft() + dx + i * dx;
            stars[i].getPos().y = pos.y;
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        for (int i = 0; i < stars.length; i++) stars[i].draw(batch);
    }
}
