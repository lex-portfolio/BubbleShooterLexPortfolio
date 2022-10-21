package com.lex_portfolio.bubble_shooter.level_editor;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lex_portfolio.bubble_shooter.level_editor.screens.main_screen.LevelEditorScreen;
import com.lex_portfolio.engine.base.BaseGame;

public class LevelEditor extends BaseGame {


    @SuppressWarnings("CommentedOutCode")
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//        float aspect = 480f / 854f;
//        float aspect = 9f / 16f;
        float aspect = 3f / 4f;
        config.setForegroundFPS(60);
        int height = 900;
        int width = (int) (height * aspect);
        config.setWindowedMode(width, height);
        new Lwjgl3Application(new LevelEditor(), config);
    }

    private LevelEditor() {
        super(null);
    }

    @Override
    public void create() {
        super.create();
        setScreen(new LevelEditorScreen(this));
    }
}
