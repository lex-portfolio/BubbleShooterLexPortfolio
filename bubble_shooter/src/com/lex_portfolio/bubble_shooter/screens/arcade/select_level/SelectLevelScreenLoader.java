package com.lex_portfolio.bubble_shooter.screens.arcade.select_level;

import static com.lex_portfolio.bubble_shooter.river_select_stage.ArcadeRiverSelectStage.STAGES_TO_PAGE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SELECT_LEVEL_SCREEN_FILE;
import static com.lex_portfolio.bubble_shooter.saves.SaveVersions.ARCADE_SELECT_LEVEL_SCREEN_VERSION;

import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.statistics.Statistics;
import com.lex_portfolio.engine.utils.Str;
import com.lex_portfolio.engine.utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SelectLevelScreenLoader {

    private final SelectLevelsScreen screen;
    private final Statistics statistics;

    private final GameBubbleShooter game;

    SelectLevelScreenLoader(SelectLevelsScreen screen) {
        this.screen = screen;
        game = screen.getGame();
        statistics = screen.getGame().getStatistics();
    }

    void loadLastSave() {
        ArrayList<String> save = game.getSaves().readActualVersionSave(ARCADE_SELECT_LEVEL_SCREEN_FILE, ARCADE_SELECT_LEVEL_SCREEN_VERSION);
        if (save == null) {
            loadDefault();
        } else {
            parseFromSave(save);
        }
    }

    private void loadDefault() {
        screen.listRiverToPage(statistics.getLastAvailableStage() / STAGES_TO_PAGE);
    }

    void save() {
        ArrayList<String> save = game.getSaves().createSave(ARCADE_SELECT_LEVEL_SCREEN_VERSION);
        save.add("ArcadeSelectLevelScreen river_page=" + screen.getRiverPage() + " river_bottom_pos_y=" + screen.getRiverBottomPosY());
        try {
            TextFile.writeTextToFile(ARCADE_SELECT_LEVEL_SCREEN_FILE, save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseFromSave(List<String> save) {
        String line = save.get(1);
        screen.setRiverFromSave(Str.parseInt(line, "river_page="), Str.parseFloat(line, "river_bottom_pos_y="));
    }
}
