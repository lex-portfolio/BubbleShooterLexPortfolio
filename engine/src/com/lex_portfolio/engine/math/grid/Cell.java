package com.lex_portfolio.engine.math.grid;

import com.badlogic.gdx.math.Vector2;

public class Cell {

    private final Vector2 pos = new Vector2();
    private int m;
    private int n;
    private CellObject obj;
    private boolean isChecked;
    private boolean isPathToUpperRow;

    public void set(int m, int n, float posX, float posY) {
        this.m = m;
        this.n = n;
        pos.set(posX, posY);
        if (obj != null) obj.onChangeCell(this);
    }

    public void add(CellObject obj) {
        if (this.obj != null)
            throw new IllegalStateException("Cell not empty " + this + " added obj = " + obj);
        this.obj = obj;
        obj.onAddedToCell(this);
    }

    public CellObject remove() {
        if (obj == null) throw new IllegalStateException("Remove from empty cell");
        CellObject o = obj;
        obj = null;
        o.onRemovedFromCell(this);
        return o;
    }

    public CellObject get() {
        return obj;
    }

    public boolean isEmpty() {
        return obj == null;
    }

    public int m() {
        return m;
    }

    public int n() {
        return n;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isPathToUpperRow() {
        return isPathToUpperRow;
    }

    public void setPathToUpperRow(boolean isPathToUpperRow) {
        this.isPathToUpperRow = isPathToUpperRow;
    }

    public Vector2 getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        return m == cell.m && n == cell.n;
    }

    @Override
    public String toString() {
        return "Cell: m = " + m + " n = " + n + " pos" + pos + " obj = " + obj;
    }

}
