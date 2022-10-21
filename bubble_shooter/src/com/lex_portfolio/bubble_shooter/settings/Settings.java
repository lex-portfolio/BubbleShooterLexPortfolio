package com.lex_portfolio.bubble_shooter.settings;

import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SETTINGS_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SETTINGS_VERSION;

import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;

public class Settings {

    public static final int MAX_PATH_TRACKERS_COUNT = 80;

    //BallsGrid
    public static final float BALL_SIZE_PERCENT = 1f;
    public static final float COLLISION_PERCENT = 0.8f;

    //Spinner
    public static final float SPINNER_HEIGHT = 0.11f;
    public static final float SPINNER_RADIUS_PERCENT = 0.72f;
    public static final Vector2 SPINNER_POS = new Vector2(0f, -0.437f);
    public static final Vector2 SPINNER_ECCENTRICITY = new Vector2(1f, 0.83f);

    //Ball PHYSICS
    private static final float DEBUG_MUL = 1f;
    public static final float COLOR_BALL_V = 3f * DEBUG_MUL;
    public static final float IRON_BALL_V = 3f * DEBUG_MUL;
    public static final float BOMB_BALL_V = 3f * DEBUG_MUL;
    public static final float FIRE_BALL_V = 0.2f * DEBUG_MUL;
    public static final float DOWNFALL_V0 = 0.1f * DEBUG_MUL;
    public static final float DOWNFALL_A = -0.5f * DEBUG_MUL;

    private static final String IS_MUSIC_ON_TOKEN = "is_music_on=";
    private static final String IS_SOUND_ON_TOKEN = "is_sound_on=";

    private boolean isMusicOn;
    private boolean isSoundOn;

    private final GameBubbleShooter game;

    public Settings(GameBubbleShooter game) {
        this.game = game;
        read();
    }

    private void read() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(SETTINGS_FILE, SETTINGS_VERSION);
        if (save == null) {
            isMusicOn = true;
            isSoundOn = true;
        } else {
            isMusicOn = Str.parseBool(save.get(1), IS_MUSIC_ON_TOKEN);
            isSoundOn = Str.parseBool(save.get(2), IS_SOUND_ON_TOKEN);
        }
    }

    public void save() {
        ArrayList<String> save = game.getSaves().createSave(SETTINGS_VERSION);
        save.add(IS_MUSIC_ON_TOKEN + isMusicOn);
        save.add(IS_SOUND_ON_TOKEN + isSoundOn);
        try {
            TextFile.writeTextToFile(SETTINGS_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMusicOn() {
        return isMusicOn;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public void setMusicOn(boolean musicOn) {
        isMusicOn = musicOn;
    }

    public void setSoundOn(boolean soundOn) {
        isSoundOn = soundOn;
    }
}
