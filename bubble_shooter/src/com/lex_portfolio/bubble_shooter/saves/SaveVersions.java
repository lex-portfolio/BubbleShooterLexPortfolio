package com.lex_portfolio.bubble_shooter.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public final class SaveVersions {

    private SaveVersions() {}

    public static final int SETTINGS_VERSION = 1;
    public static final FileHandle SETTINGS_FILE = Gdx.files.local("settings.sav");

    public static final int SCREEN_MANAGER_VERSION = 3;
    public static final FileHandle SCREEN_MANAGER_FILE = Gdx.files.local("screen_manager.sav");

    public static final int SURVIVAL_STATISTICS_VERSION = 2;
    public static final FileHandle SURVIVAL_STATISTICS_FILE = Gdx.files.local("survival_statistics.sav");

    public static final int ARCADE_STATISTICS_VERSION = 2;
    public static final FileHandle ARCADE_STATISTICS_FILE = Gdx.files.local("arcade_statistics.sav");

    public static final int SURVIVAL_SCREEN_VERSION = 5;
    public static final FileHandle SURVIVAL_SCREEN_FILE = Gdx.files.local("survival_screen.sav");

    public static final int ARCADE_SCREEN_VERSION = 6;
    public static final FileHandle ARCADE_SCREEN_FILE = Gdx.files.local("arcade_screen.sav");

    public static final int ARCADE_SELECT_LEVEL_SCREEN_VERSION = 2;
    public static final FileHandle ARCADE_SELECT_LEVEL_SCREEN_FILE = Gdx.files.local("arcade_select_level_screen.sav");
}
