package com.lex_portfolio.bubble_shooter.screens.arcade.game;

import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SCREEN_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SCREEN_VERSION;

import com.badlogic.gdx.Gdx;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.screens.common.GameScreen;
import com.lex_portfolio.bubble_shooter.screens.common.ScreenLoader;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArcadeScreenLoader extends ScreenLoader<ArcadeGameScreen> {

    private final GameBubbleShooter game;

    public ArcadeScreenLoader(ArcadeGameScreen screen) {
        super(screen);
        game = screen.getGame();
    }

    public void loadStage(int stage) {
        ArrayList<String> save = game.getSaves().readActualVersionSave(ARCADE_SCREEN_FILE, ARCADE_SCREEN_VERSION);
        if (save != null && stage == Str.parseInt(save.get(1), "stage=")) {
            parseFromSave(save);
        } else {
            loadStageFromAsset(stage);
        }
    }

    public void loadStageFromAsset(int stage) {
        List<String> save;
        try {
            save = TextFile.readTextFromFile(Gdx.files.internal("levels/level_" + stage + ".lvl"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        screen.setState(ArcadeGameScreen.State.WAITING);
        screen.setStage(stage);
        float starsLiveDuration = getStarsLiveDuration(stage);
        screen.setStarsLiveDuration(starsLiveDuration);
        screen.setStarsLiveTimer(starsLiveDuration);
        parseGrid(save, 0);
        screen.getGrid().setBottom(-0.5f);
        screen.resetCartridgeAndFillRandomBalls();
    }

    public void loadLastSave() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(ARCADE_SCREEN_FILE, ARCADE_SCREEN_VERSION);
        if (save == null) {
            throw new RuntimeException();
        } else {
            parseFromSave(save);
        }
    }

    @SuppressWarnings("unused")
    public void save(ArcadeGameScreen screen) {
        ArrayList<String> save = game.getSaves().createSave(ARCADE_SCREEN_VERSION);
        saveScreen(save);
        saveBonuses(save);
        saveGrid(save, screen.getGrid());
        saveMajorBall(save, screen.getFlyingMajorBall());
        saveBallsCartridge(save);
        saveSpinner(save);
        try {
            TextFile.writeTextToFile(ARCADE_SCREEN_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseFromSave(List<String> save) {
        parseScreen(save);
        parseBonuses(save, 2);
        int nextIndex = parseGrid(save, 3);
        nextIndex = parseFlyingMajorBall(save, nextIndex);
        nextIndex = parseBallCartridge(save, nextIndex);
        parseSpinner(save, nextIndex);
    }

    private void saveScreen(List<String> save) {
        save.add(
                "ArcadeGameScreen state=\"" + screen.getState() + "\"" +
                        " stage=" + screen.getStage() +
                        " stars_live_timer=" + screen.getStarsLiveTimer()
        );
    }

    private void parseScreen(List<String> save) {
        String line = save.get(1);
        screen.setState(GameScreen.State.valueOf(Str.parseStr(line, "state=")));
        int stage = Str.parseInt(line, "stage=");
        screen.setStage(stage);
        screen.setStarsLiveTimer(Str.parseFloat(line, "stars_live_timer="));
        screen.setStarsLiveDuration(getStarsLiveDuration(stage));
    }

    private float getStarsLiveDuration(int stage) {
        if (stage <= 30) {
            return 120f;
        } else if (stage <= 50) {
            return 180f;
        } else if (stage <= 150) {
            return 280f;
        } else if (stage <= 250) {
            return 340f;
        } else if (stage <= 650) {
            return 450f;
        } else {
            return 500f;
        }
    }
}
