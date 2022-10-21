package com.lex_portfolio.engine.math.grid.hex_grid;

import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.math.grid.Cell;

import java.util.ArrayList;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class NonRegularHexGrid extends HexGrid {

    protected final Cell[][] cells;

    public NonRegularHexGrid(int m, int n, boolean isShortBase) {
        super(m, n);
        cells = new Cell[m][];
        for (int i = 0; i < m; i++) {
            int cols;
            if (isShortBase) {
                cols = n - 1 + i % 2;
            } else {
                cols = n - i % 2;
            }
            cells[i] = new Cell[cols];
            for (int j = 0; j < cols; j++) cells[i][j] = new Cell();
        }
    }

    @Override
    public void setWidthProportion(float width) {
        super.setWidthProportion(width);
        reCalculateCellsParams();
    }

    @Override
    public void setBottom(float bottom) {
        super.setBottom(bottom);
        reCalculateCellsParams();
    }

    public void setPosY(float posY) {
        pos.y = posY;
        reCalculateCellsParams();
    }

    public void reCalculateCellsParams() {
        final float bottom = getBottom();
        final float left = getLeft();
        final float dx = 2f * r;
        final float dy = 1.5f * R;
        for (int i = 0; i < m; i++) {
            float shift = 0;
            if (isShortLine(i)) shift = r;
            int cols = cells[i].length;
            for (int j = 0; j < cols; j++) {
                float posX = left + shift + r + j * dx;
                float posY = bottom + R + i * dy;
                cells[i][j].set(i, j, posX, posY);
            }
        }
    }

    public void shiftDown() {
        if (m % 2 != 0) throw new RuntimeException("ShiftDown() in odd grid. m: " + m);
        Cell[] row0 = cells[0];
        System.arraycopy(cells, 1, cells, 0, cells.length - 1);
        cells[m - 1] = row0;
        reCalculateCellsParams();
    }

    public Cell getCell(int m, int n) {
        return cells[m][n];
    }

    public int getRowLength(int row) {
        return cells[row].length;
    }

    public Cell getLastRowCell(int col) {
        return cells[m - 1][col];
    }

    public int getLastRowLength() {
        return cells[m - 1].length;
    }

    public int getLastRowIndex() {
        return m - 1;
    }

    @SuppressWarnings("unused")
    public Cell getCellReference(Vector2 pos) {
        if (!isMe(pos)) throw new RuntimeException("Outside grid pos = " + pos);
        int minM = 0, minN = 0;
        float minDist2 = pos.dst2(cells[minM][minN].getPos());
        for (int i = 0; i < m; i++) {
            for (int j = 0, cnt = cells[i].length; j < cnt; j++) {
                float dist2 = cells[i][j].getPos().dst2(pos);
                if (dist2 < minDist2) {
                    minDist2 = dist2;
                    minM = i;
                    minN = j;
                }
            }
        }
        return cells[minM][minN];
    }

    public Cell getCell(Vector2 pos) {
        //Столько проверок необходимо для отладки
        if (!isMe(pos)) {
            if (pos.x < getLeft()) {
                if (pos.y < getBottom()) {
                    throw new RuntimeException();
                } else if (pos.y > getTop()) {
                    throw new RuntimeException();
                } else {
                    throw new RuntimeException();
                }
            } else if (pos.x > getRight()) {
                if (pos.y < getBottom()) {
                    throw new RuntimeException();
                } else if (pos.y > getTop()) {
                    throw new RuntimeException();
                } else {
                    throw new RuntimeException();
                }
            } else {
                if (pos.y < getBottom()) {
                    throw new RuntimeException();
                } else if (pos.y > getTop()) {
                    return null;
                } else {
                    throw new RuntimeException();
                }
            }
        }
        final float dy = Math.abs(getBottom() - pos.y);
        final float dx = Math.abs(getLeft() - pos.x);
        int centerM = (int) (dy / (1.5f * R));
        int centerN = (int) (dx / (2f * r));
        //TODO: доработать
        if (centerM == m) centerM--;
        if (centerN == cells[centerM].length) centerN--;
        return getNearCellFlower(pos, cells[centerM][centerN]);
    }

    private final ArrayList<Cell> flower = new ArrayList<>(7);

    private void getFlower(Cell center) {
        flower.clear();
        flower.add(center);
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell cell = getNeighbor(center, sidesOfWorld[i]);
            if (cell != null) flower.add(cell);
        }
    }

    private Cell getNearCellFlower(Vector2 pos, Cell center) {
        final float r2 = r * r;
        float minDist2 = pos.dst2(center.getPos());
        if (minDist2 <= r2) return center;
        getFlower(center);
        int min = 0;
        final int cnt = flower.size();
        for (int i = 1; i < cnt; i++) {
            Cell cell = flower.get(i);
            float dist2 = cell.getPos().dst2(pos);
            if (dist2 <= r2) {
                return cell;
            } else if (dist2 < minDist2) {
                minDist2 = dist2;
                min = i;
            }
        }
        return flower.get(min);
    }

    public boolean cellNotExist(int row, int col) {
        if (row < 0 || row >= m) return true;
        return col < 0 || col >= cells[row].length;
    }

    public boolean isShortLine(int i) {
        int length = cells[i].length;
        if (length == n - 1) {
            return true;
        } else if (length == n) {
            return false;
        } else {
            throw new RuntimeException("Unknown length m: " + m + " length: " + length);
        }
    }

    public Cell getNeighbor(Cell cell, int sideOfWorld) {
        int m = cell.m();
        int n = cell.n();
        final boolean isShortLine = isShortLine(m);
        switch (sideOfWorld) {
            case W:
                if (cellNotExist(m, --n)) return null;
                return cells[m][n];
            case NW:
                if (isShortLine) {
                    if (cellNotExist(++m, n)) return null;
                } else {
                    if (cellNotExist(++m, --n)) return null;
                }
                return cells[m][n];
            case NE:
                if (isShortLine) {
                    if (cellNotExist(++m, ++n)) return null;
                } else {
                    if (cellNotExist(++m, n)) return null;
                }
                return cells[m][n];
            case E:
                if (cellNotExist(m, ++n)) return null;
                return cells[m][n];
            case SE:
                if (isShortLine) {
                    if (cellNotExist(--m, ++n)) return null;
                } else {
                    if (cellNotExist(--m, n)) return null;
                }
                return cells[m][n];
            case SW:
                if (isShortLine) {
                    if (cellNotExist(--m, n)) return null;
                } else {
                    if (cellNotExist(--m, --n)) return null;
                }
                return cells[m][n];
            default:
                throw new RuntimeException("Unknown sideOfWorld: " + sideOfWorld);
        }
    }

    protected void resetIsPathToUpperRowStatus() {
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) cells[i][j].setPathToUpperRow(false);
        }
    }

    protected void resetCheckedStatus() {
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) cells[i][j].setChecked(false);
        }
    }

    public boolean isPathToUpperRow(Cell cell) {
        resetCheckedStatus();
        return isPathToUpperRowRecursive(cell);
    }

    private boolean isPathToUpperRowRecursive(Cell cell) {
        if (cell.isChecked()) throw new IllegalStateException("Repeat checking cell: " + cell);
        if (cell.isEmpty()) throw new IllegalStateException("cell.isEmpty()");
        cell.setChecked(true);
        if (cell.m() == m - 1) return true;
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell neighbor = getNeighbor(cell, sidesOfWorld[i]);
            if (neighbor == null || neighbor.isEmpty() || neighbor.isChecked()) continue;
            if (isPathToUpperRowRecursive(neighbor)) return true;
        }
        return false;
    }

    public int findLowerNotEmptyRow() {
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                if (!cells[i][j].isEmpty()) return i;
            }
        }
        return -1;
    }

    public int getNotEmptyRowsCount() {
        int f = findLowerNotEmptyRow();
        if (f == -1) {
            return 0;
        } else {
            return m - f;
        }
    }

    public float getRowBottom(int row) {
        return cells[row][0].getPos().y - R;
    }

    public boolean isUpperRow(Cell cell) {
        return cell.m() == m - 1;
    }

    public boolean isShortBaseLine() {
        return isShortLine(0);
    }
}
