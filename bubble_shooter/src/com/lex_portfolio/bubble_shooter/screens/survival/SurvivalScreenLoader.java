package com.lex_portfolio.bubble_shooter.screens.survival;

import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SURVIVAL_SCREEN_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SURVIVAL_SCREEN_VERSION;

import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.bubble_shooter.screens.common.GameScreen;
import com.lex_portfolio.bubble_shooter.screens.common.ScreenLoader;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SurvivalScreenLoader extends ScreenLoader<SurvivalGameScreen> {

    private static final int DEFAULT_M = 26;
    private static final int DEFAULT_N = 14;
    private static final int NEW_GAME_EMPTY_ROWS = 15;

    private final SurvivalStageManager stageManager;
    private final SurvivalFillGridManager fillGridManager;

    private final GameBubbleShooter game;

    SurvivalScreenLoader(SurvivalGameScreen screen, SurvivalStageManager stageManager, SurvivalFillGridManager fillGridManager) {
        super(screen);
        game = screen.getGame();
        this.stageManager = stageManager;
        this.fillGridManager = fillGridManager;
    }

    void loadNewGame() {
        screen.setState(GameScreen.State.WAITING);
        screen.setStage(0);
        screen.setFrags(0);
        screen.setRequiredFragsForNextStage(stageManager.getRequiredFragsForNextStage(0));
        screen.setRegularSpeed(stageManager.getSpeed(0));
        screen.setSpeedUpTimer(0f);
        screen.setShiftDownTimer(0f);
        screen.setNextStageTimer(0f);
        screen.setFireballProgress(0f);
        screen.setBombProgress(0f);

        screen.createNewGrid(DEFAULT_M, DEFAULT_N, false);
        fillGridManager.fillNewStage(
                screen.getGrid(),
                stageManager.getMaxRandomColorCount(0),
                stageManager.getFillPercent(0),
                stageManager.getIronPercent(0),
                NEW_GAME_EMPTY_ROWS
        );
        screen.resetCartridgeAndFillRandomBalls();
    }

    @SuppressWarnings("unused")
    void loadStage(int stage) {
        screen.setState(GameScreen.State.WAITING);
        screen.setStage(stage);
        screen.setRequiredFragsForNextStage(stageManager.getRequiredFragsForNextStage(stage));
        screen.setRegularSpeed(stageManager.getSpeed(stage));
        screen.setSpeedUpTimer(0f);
        screen.setShiftDownTimer(0f);
        screen.setNextStageTimer(0f);

        fillGridManager.fillNewStage(
                screen.getGrid(),
                stageManager.getMaxRandomColorCount(stage),
                stageManager.getFillPercent(stage),
                stageManager.getIronPercent(stage),
                NEW_GAME_EMPTY_ROWS
        );
        screen.resetCartridgeAndFillRandomBalls();
    }

    void loadLastSave() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(SURVIVAL_SCREEN_FILE, SURVIVAL_SCREEN_VERSION);
        if (save == null) {
            loadNewGame();
        } else {
            parseFromSave(save);
        }
    }

    @SuppressWarnings("unused")
    void save() {
        ArrayList<String> save = game.getSaves().createSave(SURVIVAL_SCREEN_VERSION);
        saveScreen(save);
        saveBonuses(save);
        saveStageColors(save, screen.getStageColors());
        saveGrid(save, screen.getGrid());
        saveMajorBall(save, screen.getFlyingMajorBall());
        saveBallsCartridge(save);
        saveSpinner(save);
        try {
            TextFile.writeTextToFile(SURVIVAL_SCREEN_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseFromSave(List<String> save) {
        parseScreen(save);
        parseBonuses(save, 2);
        int nextIndex = parseStageColors(save, 3);
        nextIndex = parseGrid(save, nextIndex);
        nextIndex = parseFlyingMajorBall(save, nextIndex);
        nextIndex = parseBallCartridge(save, nextIndex);
        parseSpinner(save, nextIndex);
    }

    private void saveScreen(List<String> save) {
        save.add(
                "SurvivalGameScreen state=\"" + screen.getState() + "\"" +
                        " stage=" + screen.getStage() +
                        " frags=" + screen.getFrags() +
                        " required_frags_for_next_stage=" + screen.getRequiredFragsForNextStage() +
                        " speed=" + screen.getRegularSpeed() +
                        " speed_up_timer=" + screen.getSpeedUpTimer() +
                        " shift_down_timer=" + screen.getShiftDownTimer() +
                        " next_stage_timer=" + screen.getNextStageTimer()
        );
    }

    private void parseScreen(List<String> save) {
        String line = save.get(1);
        screen.setState(GameScreen.State.valueOf(Str.parseStr(line, "state=")));
        screen.setStage(Str.parseInt(line, "stage="));
        screen.setFrags(Str.parseInt(line, "frags="));
        screen.setRequiredFragsForNextStage(Str.parseInt(line, "required_frags_for_next_stage="));
        screen.setRegularSpeed(Str.parseInt(line, "speed="));
        screen.setSpeedUpTimer(Str.parseFloat(line, "speed_up_timer="));
        screen.setShiftDownTimer(Str.parseFloat(line, "shift_down_timer="));
        screen.setNextStageTimer(Str.parseFloat(line, "next_stage_timer="));
    }

    private void saveStageColors(List<String> save, ArrayList<Ball.Type> colors) {
        int cnt = colors.size();
        StringBuilder sb = new StringBuilder("StageColors count=" + cnt);
        for (int i = 0; i < cnt; i++) {
            sb.append(" type_").append(i).append("=\"").append(colors.get(i)).append("\"");
        }
        save.add(sb.toString());
    }

    @SuppressWarnings("SameParameterValue")
    private int parseStageColors(List<String> save, int lineIndex) {
        String line = save.get(lineIndex);
        int colorsCount = Str.parseInt(line, "count=");
        ArrayList<Ball.Type> colors = new ArrayList<>(colorsCount);
        for (int i = 0; i < colorsCount; i++) {
            colors.add(Ball.Type.valueOf(Str.parseStr(line, "type_" + i + "=")));
        }
        screen.setStageColors(colors);
        return lineIndex + 1;
    }
}
