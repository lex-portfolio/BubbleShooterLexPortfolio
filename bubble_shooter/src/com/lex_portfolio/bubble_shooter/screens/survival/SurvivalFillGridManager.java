package com.lex_portfolio.bubble_shooter.screens.survival;

import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.BallsGrid;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.hex_grid.HexGrid;

import java.util.ArrayList;

class SurvivalFillGridManager {

    private final PoolBalls poolBalls;
    private final ArrayList<Ball.Type> colors = new ArrayList<>(Ball.Type.getMaxColorCount());

    SurvivalFillGridManager(PoolBalls poolBalls) {
        this.poolBalls = poolBalls;
    }

    ArrayList<Ball.Type> getColors() {
        return colors;
    }

    void setColors(ArrayList<Ball.Type> arr) {
        colors.clear();
        colors.addAll(arr);
    }

    private void addRandomColor() {
        while (true) {
            Ball.Type type = Ball.Type.values()[(int) (Math.random() * Ball.Type.getMaxColorCount())];
            if (!colors.contains(type)) {
                colors.add(type);
                return;
            }
        }
    }

    void fillNewStage(BallsGrid grid, int maxRandomColorCount, float percentFills, float ironPercent, int emptyRows) {
        colors.clear();
        for (int i = 0; i < maxRandomColorCount; i++) {
            addRandomColor();
        }
        final int rows = grid.m();
        for (int i = emptyRows; i < rows; i++) {
            final int cols = grid.getRowLength(i);
            for (int j = 0; j < cols; j++) {
                if (Math.random() < percentFills) {
                    if (Math.random() < ironPercent) {
                        grid.getCell(i, j).add(poolBalls.obtain(Ball.Type.IRON));
                    } else {
                        grid.getCell(i, j).add(poolBalls.obtain(colors.get((int) (Math.random() * colors.size()))));
                    }
                }
            }
        }
        correctGrid(grid);
    }

    @SuppressWarnings("unused")
    void shiftDownAndFillRandom(BallsGrid grid, float percentFills, float ironPercent) {
        grid.shiftDown();
        final int cnt = grid.getLastRowLength();
        for (int i = 0; i < cnt; i++) {
            Cell cell = grid.getLastRowCell(i);
            if (Math.random() < percentFills) {
                if (Math.random() < ironPercent) {
                    cell.add(poolBalls.obtain(Ball.Type.IRON));
                } else {
                    cell.add(poolBalls.obtain(colors.get((int) (Math.random() * colors.size()))));
                }
            }
        }
        correctRow(grid, grid.getLastRowIndex());
    }


    private void correctGrid(BallsGrid grid) {
        int cnt = grid.m();
        for (int i = 0; i < cnt; i++) {
            correctRow(grid, i);
        }
    }

    private void correctRow(BallsGrid grid, int row) {
        final int cnt = grid.getRowLength(row);
        for (int i = 0; i < cnt; i++) {
            Cell cell = grid.getCell(row, i);
            if (!cell.isEmpty()) continue;
            Cell leftNbr = grid.getNeighbor(cell, HexGrid.SW);
            Cell rightNbr = grid.getNeighbor(cell, HexGrid.SE);
            if (leftNbr != null && !leftNbr.isEmpty() && !grid.isPathToUpperRow(leftNbr)) {
                cell.add(poolBalls.obtain(colors.get((int) (Math.random() * colors.size()))));
            } else if (rightNbr != null && !rightNbr.isEmpty() && !grid.isPathToUpperRow(rightNbr)) {
                cell.add(poolBalls.obtain(colors.get((int) (Math.random() * colors.size()))));
            }
        }
    }
}
