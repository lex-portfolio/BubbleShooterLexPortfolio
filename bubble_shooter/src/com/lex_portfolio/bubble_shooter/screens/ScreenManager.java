package com.lex_portfolio.bubble_shooter.screens;

import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SCREEN_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SCREEN_VERSION;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SCREEN_MANAGER_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.SCREEN_MANAGER_VERSION;

import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.screens.arcade.game.ArcadeGameScreen;
import com.lex_portfolio.bubble_shooter.screens.arcade.select_level.SelectLevelsScreen;
import com.lex_portfolio.bubble_shooter.screens.help.HelpScreen;
import com.lex_portfolio.bubble_shooter.screens.statistics.StatisticsScreen;
import com.lex_portfolio.bubble_shooter.screens.survival.SurvivalGameScreen;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.saves.Saves;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;

public class ScreenManager {

    private static final String TOKEN_SCREEN = "ScreenType=";
    private static final String TOKEN_SURVIVAL_SCREEN = "SURVIVAL";
    private static final String TOKEN_STATISTICS_SCREEN = "STATISTICS";
    private static final String TOKEN_HELP_SCREEN = "HELP";
    private static final String TOKEN_ARCADE_SCREEN = "ARCADE";
    private static final String TOKEN_ARCADE_SELECT_LEVEL_SCREEN = "ARCADE_SELECT_LEVEL";

    private final GameBubbleShooter game;

    public ScreenManager(GameBubbleShooter game) {
        this.game = game;
    }

    public Base2DScreen<GameBubbleShooter> getLastScreen() {
        Saves saves = game.getSaves();
        ArrayList<String> save = saves.readActualVersionSave(SCREEN_MANAGER_FILE, SCREEN_MANAGER_VERSION);
        if (save == null) {
            return new SelectLevelsScreen(game);
        }
        String screenName = Str.parseStr(save.get(1), TOKEN_SCREEN);
        switch (screenName) {
            case TOKEN_SURVIVAL_SCREEN:
                return new SurvivalGameScreen(game);
            case TOKEN_STATISTICS_SCREEN:
                return new StatisticsScreen(game);
            case TOKEN_HELP_SCREEN:
                return new HelpScreen(game);
            case TOKEN_ARCADE_SCREEN:
                save = saves.readActualVersionSave(ARCADE_SCREEN_FILE, ARCADE_SCREEN_VERSION);
                if (save == null) {
                    return new SelectLevelsScreen(game);
                } else {
                    return new ArcadeGameScreen(game);
                }
            case TOKEN_ARCADE_SELECT_LEVEL_SCREEN:
                return new SelectLevelsScreen(game);
            default:
                throw new RuntimeException("Unknown screenName: " + screenName);
        }
    }

    public void saveLastScreen(Base2DScreen<GameBubbleShooter> screen) {
        ArrayList<String> save = game.getSaves().createSave(SCREEN_MANAGER_VERSION);
        if (screen instanceof SurvivalGameScreen) {
            save.add(TOKEN_SCREEN + "\"" + TOKEN_SURVIVAL_SCREEN + "\"");
        } else if (screen instanceof StatisticsScreen) {
            save.add(TOKEN_SCREEN + "\"" + TOKEN_STATISTICS_SCREEN + "\"");
        } else if (screen instanceof HelpScreen) {
            save.add(TOKEN_SCREEN + "\"" + TOKEN_HELP_SCREEN + "\"");
        } else if (screen instanceof ArcadeGameScreen) {
            save.add(TOKEN_SCREEN + "\"" + TOKEN_ARCADE_SCREEN + "\"");
        } else if (screen instanceof SelectLevelsScreen) {
            save.add(TOKEN_SCREEN + "\"" + TOKEN_ARCADE_SELECT_LEVEL_SCREEN + "\"");
        } else {
            throw new RuntimeException("Unknown screen: " + screen);
        }
        try {
            TextFile.writeTextToFile(SCREEN_MANAGER_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}