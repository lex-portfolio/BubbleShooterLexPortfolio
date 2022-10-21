package com.lex_portfolio.bubble_shooter.level_editor.screens.main_screen;

import com.badlogic.gdx.Gdx;
import com.lex_portfolio.bubble_shooter.screens.common.GridLoader;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class MainScreenLoader extends GridLoader<LevelEditorScreen> {

    MainScreenLoader(LevelEditorScreen screen) {
        super(screen, screen.getPoolBalls());
    }

    MainScreenLoader save(LevelEditorScreen screen) {
        screen.getGrid().setBottom(-0.5f);
        ArrayList<String> save = new ArrayList<>();
        saveGrid(save, screen.getGrid());
        try {
            TextFile.writeTextToFile(Gdx.files.absolute(getSavePath() + "level_" + screen.getStage() + ".lvl"), save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    void load(int stage) {
        List<String> save;
        try {
            save = TextFile.readTextFromFile(Gdx.files.absolute(getSavePath() + "level_" + stage + ".lvl"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        screen.setStage(stage);
        parseGrid(save, 0);
    }

    private String getSavePath() {
        String savePath = Paths.get("").toAbsolutePath().getParent().getParent().toString();
        savePath += "\\bubble_shooter_android\\assets\\levels\\";
        return savePath;
    }
}