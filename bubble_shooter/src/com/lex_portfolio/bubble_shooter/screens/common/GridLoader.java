package com.lex_portfolio.bubble_shooter.screens.common;

import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.balls.BallsGrid;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.engine.math.grid.Cell;
import com.lex_portfolio.engine.utils.Str;

import java.util.List;

public class GridLoader<T extends BallsGridScreen> {

    protected final T screen;
    final PoolBalls poolBalls;

    public GridLoader(T screen, PoolBalls poolBalls) {
        this.screen = screen;
        this.poolBalls = poolBalls;
    }

    protected void saveGrid(List<String> save, BallsGrid grid) {
        final int m = grid.m();
        final int n = grid.n();
        save.add("BeginGrid m=" + m + " n=" + n + " short_base=" + grid.isShortBaseLine() + " bottom=" + grid.getBottom());
        for (int i = 0; i < m; i++) {
            final int cnt = grid.getRowLength(i);
            for (int j = 0; j < cnt; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.isEmpty()) continue;
                Ball ball = (Ball) cell.get();
                if (!ball.inCell()) {
                    throw new IllegalStateException("ball.state: " + ball.getState());
                }
                save.add("\tBall m=" + i + " n=" + j + " type=\"" + ball.getType() + "\"");
            }
        }
        save.add("EndGrid");
    }

    protected int parseGrid(List<String> save, int saveLineIndex) {
        String line = save.get(saveLineIndex);
        int m = Str.parseInt(line, "m=");
        int n = Str.parseInt(line, "n=");
        boolean isShortBase = Str.parseBool(line, "short_base=");
        float bottom = Str.parseFloat(line, "bottom=");
        screen.createNewGrid(m, n, isShortBase);
        saveLineIndex++;
        BallsGrid grid = screen.getGrid();
        for (int i = saveLineIndex; i < save.size(); i++) {
            line = save.get(i).trim();
            if (line.startsWith("EndGrid")) {
                grid.setBottom(bottom);
                return i + 1;
            }
            Ball.Type type = Ball.Type.valueOf(Str.parseStr(line, "type="));
            m = Str.parseInt(line, "m=");
            n = Str.parseInt(line, "n=");
            Ball ball = poolBalls.obtain(type);
            grid.getCell(m, n).add(ball);
        }
        throw new RuntimeException();
    }
}