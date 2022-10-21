package com.lex_portfolio.engine.math.world_bounds;

import com.lex_portfolio.engine.math.Rect;

public class MaxPortraitBounds extends Rect {

    private static final float ASPECT = 3f / 4f;

    public void setBoundsWidth(float width) {
        setWidthProportion(width, ASPECT);
    }

    public void setBoundsHeight(float height) {
        setHeightProportion(height, ASPECT);
    }
}
