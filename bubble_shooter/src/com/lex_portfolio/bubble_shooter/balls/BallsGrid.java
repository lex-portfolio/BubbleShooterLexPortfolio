package com.lex_portfolio.bubble_shooter.balls;

import com.lex_portfolio.engine.math.Rnd;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.hex_grid.NonRegularHexGrid;

import java.util.ArrayList;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class BallsGrid extends NonRegularHexGrid {

    private final ArrayList<Ball.Type> availableColorTypes = new ArrayList<>(Ball.Type.getMaxColorCount());

    public BallsGrid(int m, int n, boolean shortBase) {
        super(m, n, shortBase);
    }

    public int boomBalls(Cell cell) {
        if (cell.isEmpty()) throw new IllegalArgumentException();
        int result = 0;
        Ball ball = (Ball) cell.remove();
        ball.delete(Rnd.nextFloat(0f, 0.1f));
        result++;
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell neighborNear = getNeighbor(cell, sidesOfWorld[i]);
            if (neighborNear == null) continue;
            if (!neighborNear.isEmpty()) {
                ball = (Ball) neighborNear.remove();
                ball.delete(Rnd.nextFloat(0f, 0.1f));
                result++;
            }
            for (int j = 0; j < sidesOfWorld.length; j++) {
                Cell neighborFar = getNeighbor(neighborNear, sidesOfWorld[j]);
                if (neighborFar != null && !neighborFar.isEmpty()) {
                    ball = (Ball) neighborFar.remove();
                    ball.delete(Rnd.nextFloat(0f, 0.1f));
                    result++;
                }
            }
        }
        return result;
    }

    private final ArrayList<Cell> colorBlockCells = new ArrayList<>();
    private final ArrayList<Ball> deletedBalls = new ArrayList<>();

    public ArrayList<Ball> deleteColorBlock(Cell startCell) {
        deletedBalls.clear();
        getColorBlock(colorBlockCells, startCell);
        int cnt = colorBlockCells.size();
        if (cnt >= 3) {
            for (int i = 0; i < cnt; i++) {
                Ball ball = (Ball) colorBlockCells.get(i).remove();
                ball.delete(Rnd.nextFloat(0f, 0.1f));
                deletedBalls.add(ball);
            }
        }
        colorBlockCells.clear();
        return deletedBalls;
    }

    private void getColorBlock(ArrayList<Cell> blockCells, Cell startCell) {
        if (!blockCells.isEmpty()) throw new IllegalStateException("Not empty color blockCells.");
        if (startCell.isEmpty()) throw new IllegalStateException("Empty startCell.");
        Ball ball = (Ball) startCell.get();
        Ball.Type type = ball.getType();
        if (!type.isColored()) throw new IllegalStateException("type: " + type);
        resetCheckedStatus();
        addToColorBlock(blockCells, startCell, type);
    }

    private void addToColorBlock(ArrayList<Cell> blockCells, Cell cell, Ball.Type requiredType) {
        if (cell.isChecked()) throw new IllegalStateException("Repeat checking cell: " + cell);
        if (cell.isEmpty()) throw new IllegalStateException("cell.isEmpty()");
        cell.setChecked(true);
        Ball ball = (Ball) cell.get();
        Ball.Type currentType = ball.getType();
        if (currentType != requiredType) return;
        blockCells.add(cell);
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell neighbor = getNeighbor(cell, sidesOfWorld[i]);
            if (neighbor == null || neighbor.isEmpty() || neighbor.isChecked()) continue;
            addToColorBlock(blockCells, neighbor, requiredType);
        }
    }

    //TODO: оптимизировать
    public Cell getCollisionBallCell(Ball majorBall, float minCenterDist) {
        final float minCenterDist2 = minCenterDist * minCenterDist;
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                Cell cell = cells[i][j];
                if (cell.isEmpty()) continue;
                Ball ball = (Ball) cell.get();
                if (majorBall.getPos().dst2(ball.getPos()) <= minCenterDist2) return cell;
            }
        }
        return null;
    }

    public int downfallNonUpperPathBalls(float vx0, float vy, float angleRange) {
        int count = 0;
        resetIsPathToUpperRowStatus();
        for (int i = 0; i < m; i++) {
            int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                Cell cell = cells[i][j];
                if (cell.isEmpty() || cell.isPathToUpperRow()) continue;
                boolean isPathToUpperRow = isPathToUpperRow(cell);
                if (isPathToUpperRow) {
                    markAllNeighborsYesPathToUpperRow(cell);
                } else {
                    count += downfallAllNeighborsRecursive(cell, vx0, vy, angleRange);
                }
            }
        }
        return count;
    }

    private void markAllNeighborsYesPathToUpperRow(Cell cell) {
        resetCheckedStatus();
        markAllNeighborsPathToUpperRowStatusRecursive(cell);
    }

    private void markAllNeighborsPathToUpperRowStatusRecursive(Cell cell) {
        if (cell.isChecked()) throw new IllegalStateException("Repeat checking cell: " + cell);
        if (cell.isEmpty()) throw new IllegalStateException("cell.isEmpty()");
        cell.setChecked(true);
        cell.setPathToUpperRow(true);
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell neighbor = getNeighbor(cell, sidesOfWorld[i]);
            if (neighbor == null || neighbor.isEmpty() || neighbor.isChecked()) continue;
            markAllNeighborsPathToUpperRowStatusRecursive(neighbor);
        }
    }

    private int downfallAllNeighborsRecursive(Cell cell, float vx0, float vy, float angleRange) {
        if (cell.isEmpty()) throw new IllegalStateException("cell.isEmpty()");
        if (cell.isPathToUpperRow()) {
            throw new IllegalStateException("Cell have path to upper row: " + cell);
        }

        Ball ball = (Ball) cell.remove();
        ball.downfall(vx0, vy, Rnd.nextFloat(0f, angleRange));
        int result = 1;
        for (int i = 0; i < sidesOfWorld.length; i++) {
            Cell neighbor = getNeighbor(cell, sidesOfWorld[i]);
            if (neighbor == null || neighbor.isEmpty()) continue;
            result += downfallAllNeighborsRecursive(neighbor, vx0, vy, angleRange);
        }
        return result;
    }

    public void downfallAllBall(float vx, float ay) {
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                Cell cell = cells[i][j];
                if (!cell.isEmpty()) {
                    ((Ball) cell.remove()).downfall(vx, ay, (int) (Math.random() * 180f));
                }
            }
        }
    }

    public Ball.Type getGridRandomColorType() {
        availableColorTypes.clear();
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                Cell cell = cells[i][j];
                if (cell.isEmpty()) continue;
                Ball.Type type = ((Ball) cell.get()).getType();
                if (type.isColored() && !availableColorTypes.contains(type)) {
                    availableColorTypes.add(type);
                }
            }
        }
        if (availableColorTypes.isEmpty()) return null;
        return availableColorTypes.get((int) (Math.random() * availableColorTypes.size()));
    }

    public boolean containsBallColor(Ball.Type type) {
        if (!type.isColored()) throw new IllegalStateException("type: " + type);
        for (int i = 0; i < m; i++) {
            final int cnt = cells[i].length;
            for (int j = 0; j < cnt; j++) {
                Cell cell = cells[i][j];
                if (cell.isEmpty()) continue;
                Ball ball = (Ball) cell.get();
                if (ball.getType().isColored() && ball.getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }
}