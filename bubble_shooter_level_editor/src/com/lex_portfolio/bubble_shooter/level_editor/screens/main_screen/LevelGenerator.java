package com.lex_portfolio.bubble_shooter.level_editor.screens.main_screen;

import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.BallsGrid;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.math.grid.hex_grid.HexGrid;

import java.util.ArrayList;

public class LevelGenerator {

    private static final int EMPTY_ROWS = 15;
    private static final float PERCENT_FILL = 0.75f;

    private final PoolBalls poolBalls;
    private final BallsGrid grid;
    private final ArrayList<Ball.Type> colors = new ArrayList<>();

    public LevelGenerator(PoolBalls poolBalls, BallsGrid grid) {
        this.poolBalls = poolBalls;
        this.grid = grid;
    }

    public void generateLevel(int level) {
        generateColorArray(getColorCount(level));
        grid.downfallAllBall(1f, 3f);
        for (int i = EMPTY_ROWS; i < grid.m(); i++) {
            int cnt = grid.getRowLength(i);
            for (int j = 0; j < cnt; j++) {
                float rnd = (float) Math.random();
                if (rnd < PERCENT_FILL) {
                    if (rnd < getIronPercent(level)) {
                        grid.getCell(i, j).add(poolBalls.obtain(Ball.Type.IRON));
                    } else {
                        grid.getCell(i, j).add(poolBalls.obtain(colors.get((int) (Math.random() * colors.size()))));
                    }
                }
            }
        }
        correctGrid();
    }

    private void correctGrid() {
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

    private int getColorCount(int level) {
        if (level < 50) {
            return 3;
        } else if (level < 150) {
            return 4;
        } else if (level < 250) {
            return 5;
        } else if (level < 650) {
            return 6;
        } else {
            return 7;
        }
    }

    private static float getIronPercent(int startLevel) {
        if (startLevel < 50) {
            return 0.01f;
        } else if (startLevel < 150) {
            return 0.02f;
        } else if (startLevel < 250) {
            return 0.03f;
        } else if (startLevel < 650) {
            return 0.05f;
        } else {
            return 0.05f;
        }
    }

    private void generateColorArray(int colorCount) {
        colors.clear();
        for (int i = 0; i < colorCount; i++) {
            addRandomColor();
        }
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
}