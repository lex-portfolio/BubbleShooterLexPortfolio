package com.lex_portfolio.engine.math.world_bounds;

import com.lex_portfolio.engine.math.Rect;

public class MinLandscapeBounds extends Rect {

    private static final float ASPECT = 854f / 480f;

    public void setBoundsWidth(float width) {
        setWidthProportion(width, ASPECT);
    }

    public void setBoundsHeight(float height) {
        setHeightProportion(height, ASPECT);
    }

}
