package com.lex_portfolio.engine.math.world_bounds;


import com.lex_portfolio.engine.math.Rect;

public class MaxLandscapeBounds extends Rect {

    private static final float ASPECT = 4f / 3f;

    public void setBoundsWidth(float width) {
        setWidthProportion(width, ASPECT);
    }

    public void setBoundsHeight(float height) {
        setHeightProportion(height, ASPECT);
    }

}
