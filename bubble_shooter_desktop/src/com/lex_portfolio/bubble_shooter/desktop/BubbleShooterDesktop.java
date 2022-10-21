package com.lex_portfolio.bubble_shooter.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;

public class BubbleShooterDesktop {

    @SuppressWarnings("CommentedOutCode")
    public static void main(String[] arg) {
//		float aspect = 480f / 854f;
//        float aspect = 9f / 16f;
        float aspect = 10f / 16f;
//        float aspect = 3f / 4f;
        int height = 900;
        int width = (int) (height * aspect);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(width, height);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new GameBubbleShooter(null), config);
    }
}
