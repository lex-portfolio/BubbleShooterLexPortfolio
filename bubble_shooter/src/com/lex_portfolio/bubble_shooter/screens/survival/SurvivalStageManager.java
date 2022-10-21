package com.lex_portfolio.bubble_shooter.screens.survival;

import com.lex_portfolio.bubble_shooter.balls.Ball;

class SurvivalStageManager {

    private static final int PERIOD = 15;

    @SuppressWarnings({"unused", "SameReturnValue"})
    int getCurrentTrackersCount(int stage) {
        return 47;
    }

    int getRequiredFragsForNextStage(int stage) {
        int colors = getMaxRandomColorCount(stage);
        switch (colors) {
            case 2:
                return 200;
            case 3:
                return 100;
            case 4:
                return 80;
            case 5:
                return 50;
            case 6:
                return 40;
            case 7:
                return 30;
            case 8:
                return 20;
            default:
                throw new IllegalStateException("Unexpected value: " + colors);
        }
    }

    private static final int MIN_COLOR_COUNT = 2;
    private static final int MAX_COLOR_COUNT = Ball.Type.getMaxColorCount();

    int getMaxRandomColorCount(int stage) {
        int colorCount = MIN_COLOR_COUNT + stage / PERIOD;
        if (colorCount > MAX_COLOR_COUNT) colorCount = MAX_COLOR_COUNT;
        return colorCount;
    }

    @SuppressWarnings({"unused", "SameReturnValue"})
    int getSpeed(int stage) {
        return 1;
    }

    private static final float MIN_IRON_PERCENT = 0f;
    private static final float MAX_IRON_PERCENT = 0.15f;
    private static final float IRON_PERCENT_PHASE_STEP = 0.02f;

    float getIronPercent(int stage) {
        int phase = stage / PERIOD;
        float ironPercent = MIN_IRON_PERCENT + phase * IRON_PERCENT_PHASE_STEP;
        if (ironPercent > MAX_IRON_PERCENT) ironPercent = MAX_IRON_PERCENT;
        return ironPercent;
    }

    private static final float MIN_FILL_PERCENT = 0.6f;
    private static final float MAX_FILL_PERCENT = 1f;
    private static final float STEP_FILL_PERCENT = 0.03f;

    float getFillPercent(int stage) {
        int phase = stage / PERIOD;
        float fillPercent = MIN_FILL_PERCENT + phase * STEP_FILL_PERCENT;
        if (fillPercent > MAX_FILL_PERCENT) fillPercent = MAX_FILL_PERCENT;
        return fillPercent;
    }
}