package com.lex_portfolio.bubble_shooter.screens.common;

import com.lex_portfolio.bubble_shooter.balls.BallsGrid;

public interface BallsGridScreen {
    BallsGrid getGrid();
    void createNewGrid(int m, int n, boolean isShortBase);
}
