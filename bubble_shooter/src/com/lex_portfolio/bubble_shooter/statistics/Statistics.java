package com.lex_portfolio.bubble_shooter.statistics;

import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_STATISTICS_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_STATISTICS_VERSION;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SURVIVAL_STATISTICS_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SURVIVAL_STATISTICS_VERSION;

import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;

public class Statistics {

    private static final String HI_SCORE_TOKEN = "hi_score=";
    private static final String LAST_AVAILABLE_STAGE_TOKEN = "last_available_stage=";
    private static final String STAGE_TOKEN = "stage=";
    private static final String STARS_TOKEN = "stars=";
    public static final int ARCADE_STAGE_COUNT = 996;

    private final int[] arcadeStagesStars = new int[ARCADE_STAGE_COUNT];
    private int lastArcadeAvailableStage;

    private int survivalHiScore;

    private final GameBubbleShooter game;

    public Statistics(GameBubbleShooter game) {
        this.game = game;
        readSurvivalStatistics();
        readArcadeStatistics();
    }

    public void save() {
        saveSurvivalStatistics();
        saveArcadeStatistics();
    }

    private void readSurvivalStatistics() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(SURVIVAL_STATISTICS_FILE, SURVIVAL_STATISTICS_VERSION);
        if (save == null) {
            survivalHiScore = 0;
        } else {
            survivalHiScore = Str.parseInt(save.get(1), HI_SCORE_TOKEN);
        }
    }

    private void saveSurvivalStatistics() {
        ArrayList<String> save = game.getSaves().createSave(SURVIVAL_STATISTICS_VERSION);
        save.add(HI_SCORE_TOKEN + survivalHiScore);
        try {
            TextFile.writeTextToFile(SURVIVAL_STATISTICS_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readArcadeStatistics() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(ARCADE_STATISTICS_FILE, ARCADE_STATISTICS_VERSION);
        if (save == null) {
            lastArcadeAvailableStage = 0;
        } else {
            lastArcadeAvailableStage = Str.parseInt(save.get(1), LAST_AVAILABLE_STAGE_TOKEN);
            final int starLineIndex = 2;
            for (int i = 0; i < arcadeStagesStars.length; i++) {
                String line = save.get(starLineIndex + i).trim();
                arcadeStagesStars[i] = Str.parseInt(line, STARS_TOKEN);
            }
        }
    }

    private void saveArcadeStatistics() {
        ArrayList<String> save = game.getSaves().createSave(ARCADE_STATISTICS_VERSION);
        save.add(LAST_AVAILABLE_STAGE_TOKEN + lastArcadeAvailableStage);
        for (int i = 0; i < arcadeStagesStars.length; i++) {
            save.add(STAGE_TOKEN + i + " " + STARS_TOKEN + arcadeStagesStars[i]);
        }
        try {
            TextFile.writeTextToFile(ARCADE_STATISTICS_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getStars(int stage) {
        return arcadeStagesStars[stage];
    }

    public void setStageStars(int stage, int stars) {
        arcadeStagesStars[stage] = stars;
    }

    public int getLastAvailableStage() {
        return lastArcadeAvailableStage;
    }

    public void setLastAvailableStage(int lastAvailableStage) {
        this.lastArcadeAvailableStage = lastAvailableStage;
    }

    public int getSurvivalHiScore() {
        return survivalHiScore;
    }

    public void setSurvivalHiScore(int survivalHiScore) {
        this.survivalHiScore = survivalHiScore;
    }
}
