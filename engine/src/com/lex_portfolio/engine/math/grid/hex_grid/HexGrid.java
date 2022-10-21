package com.lex_portfolio.engine.math.grid.hex_grid;

import com.lex_portfolio.engine.math.Rect;

public class HexGrid extends Rect {

    public static final int W = 0, NW = 1, NE = 2, E = 3, SE = 4, SW = 5;
    protected static final int[] sidesOfWorld = {W, NW, NE, E, SE, SW};
    protected static final float sin60 = (float) Math.sin(Math.toRadians(60));

    protected final int m;
    protected final int n;
    protected float r;
    protected float R;

    public HexGrid(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @SuppressWarnings("unused")
    public void setWidthProportion(float width) {
        halfWidth = 0.5f * width;
        r = 0.5f * width / n;
        R = r / sin60;
        int pairs = m / 2;
        halfHeight = 0.5f * R * (3 * pairs + 0.5f + 1.5f * (m % 2));
    }

    public int m() {
        return m;
    }

    public int n() {
        return n;
    }

    public float R() {
        return R;
    }

    public float r() {
        return r;
    }
}