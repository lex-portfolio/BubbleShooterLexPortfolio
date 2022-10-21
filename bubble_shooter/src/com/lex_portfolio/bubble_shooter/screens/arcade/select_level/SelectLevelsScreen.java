package com.lex_portfolio.bubble_shooter.screens.arcade.select_level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.dialogs.settings.DialogSettings;
import com.lex_portfolio.bubble_shooter.dialogs.settings.DialogSettingsListener;
import com.lex_portfolio.bubble_shooter.river_select_stage.ArcadeRiverSelectStage;
import com.lex_portfolio.bubble_shooter.river_select_stage.SelectStageListener;
import com.lex_portfolio.bubble_shooter.screens.arcade.game.ArcadeGameScreen;
import com.lex_portfolio.bubble_shooter.screens.help.HelpScreen;
import com.lex_portfolio.bubble_shooter.screens.survival.SurvivalGameScreen;
import com.lex_portfolio.bubble_shooter.settings.Settings;
import com.lex_portfolio.engine.base.Base2DScreen;
import com.lex_portfolio.engine.base.BaseTextureAtlas;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.base.TypeSize;
import com.lex_portfolio.engine.math.Rect;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;

public class SelectLevelsScreen extends Base2DScreen<GameBubbleShooter> implements SelectStageListener, ActionListener, DialogSettingsListener {

    private static final float WORLD_HEIGHT = 1f;
    private static final float BUTTONS_HEIGHT = 0.08f;

    private enum State {MAIN, MENU, SETTINGS}

    private final SelectLevelScreenLoader loader;

    private State state = State.MAIN;
    private Sprite bg;
    private ScaledTouchUpButton btnRateUs;
    private ScaledTouchUpButton btnArcade;
    private ScaledTouchUpButton btnSurvival;
    private ScaledTouchUpButton btnPuzzle;
    private ScaledTouchUpButton btnSettings;
    private ScaledTouchUpButton bntHelp;
    private ScaledTouchUpButton btnMenu;
    private ScaledTouchUpButton btnCloseMenu;
    private ScaledTouchUpButton btnBack;
    private DialogSettings dialogSettings;

    private ArcadeRiverSelectStage river;

    public SelectLevelsScreen(GameBubbleShooter game) {
        super(game, WORLD_HEIGHT, TypeSize.HEIGHT);
        loader = new SelectLevelScreenLoader(this);
    }

    @SuppressWarnings("unused")
    @Override
    public void show() {
        super.show();
        game.getScreenManager().saveLastScreen(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        BaseTextureAtlas atlas = game.getAtlas("game.atlas");
        Settings settings = game.getSettings();

        TextureRegion regionDialogSettingsBg = atlas.getRegion("bg_dialog_settings");
        TextureRegion regionBtnMusic = atlas.getRegion("btn_music");
        TextureRegion regionBtnSound = atlas.getRegion("btn_sound");
        TextureRegion regionBtnClose = atlas.getRegion("btn_close");
        dialogSettings = new DialogSettings(
                regionDialogSettingsBg,
                regionBtnMusic,
                regionBtnSound,
                regionBtnClose,
                game.getFont("font_level.fnt"),
                settings.isMusicOn(),
                settings.isSoundOn(),
                this
        );

        bg = new Sprite(atlas.getRegion("choiceMenu"));
        bg.setHeightProportion(0.5f);
        btnCloseMenu = new ScaledTouchUpButton(atlas.getRegion("btn_close"), this);
        btnArcade = new ScaledTouchUpButton(atlas.getRegion("btn_arcade"), this);
        btnSurvival = new ScaledTouchUpButton(atlas.getRegion("btn_survival"), this);
        btnPuzzle = new ScaledTouchUpButton(atlas.getRegion("btn_puzzle"), this);

        btnSettings = new ScaledTouchUpButton(atlas.getRegion("btn_settings"), this);
        btnSettings.setHeightProportion(BUTTONS_HEIGHT);
        bntHelp = new ScaledTouchUpButton(atlas.getRegion("bnt_help"), this);
        bntHelp.setHeightProportion(BUTTONS_HEIGHT);
        btnRateUs = new ScaledTouchUpButton(atlas.getRegion("btn_rate"), this);
        btnRateUs.setHeightProportion(BUTTONS_HEIGHT);
        btnMenu = new ScaledTouchUpButton(atlas.getRegion("btn_menu"), this);
        btnMenu.setHeightProportion(BUTTONS_HEIGHT);
        btnBack = new ScaledTouchUpButton(atlas.getRegion("btn_back"), this);
        btnBack.setHeightProportion(BUTTONS_HEIGHT);

        TextureRegion regionMarkerLeft = atlas.getRegion("level_flame_left");
        TextureRegion regionMarkerRight = atlas.getRegion("level_flame_right");

        TextureRegion[] regionsButtonsLeft = new TextureRegion[2];
        regionsButtonsLeft[0] = atlas.getRegion("btn_level_close_left");
        regionsButtonsLeft[1] = atlas.getRegion("btn_level_open_left");

        TextureRegion[] regionsButtonsRight = new TextureRegion[2];
        regionsButtonsRight[0] = atlas.getRegion("btn_level_close_right");
        regionsButtonsRight[1] = atlas.getRegion("btn_level_open_right");

        Font font = game.getFont("font_2.fnt");

        TextureRegion[] regionLeftStars = new TextureRegion[4];
        for (int i = 0; i < regionLeftStars.length; i++) {
            regionLeftStars[i] = atlas.getRegion("level_stars_left_" + i);
        }
        TextureRegion[] regionRightStars = new TextureRegion[4];
        for (int i = 0; i < regionRightStars.length; i++) {
            regionRightStars[i] = atlas.getRegion("level_stars_right_" + i);
        }

        TextureRegion regionRiver = atlas.getRegion("level_river");

        int lastPage = 164;
        river = new ArcadeRiverSelectStage(
                regionRiver,
                regionsButtonsLeft,
                regionsButtonsRight,
                regionMarkerLeft,
                regionMarkerRight,
                regionLeftStars,
                regionRightStars,
                font,
                lastPage,
                game.getStatistics(),
                this
        );
        loader.loadLastSave();
        System.gc();
    }

    @SuppressWarnings("unused")
    @Override
    protected void resize(Rect worldBounds) {
        river.resize(worldBounds);

        btnMenu.setLeft(worldBounds.getLeft());
        btnMenu.setBottom(worldBounds.getBottom());
        btnSettings.setLeft(worldBounds.getLeft());
        btnSettings.setBottom(btnMenu.getTop());
        bntHelp.setLeft(worldBounds.getLeft());
        bntHelp.setBottom(btnSettings.getTop());
        btnRateUs.setRight(worldBounds.getRight());
        btnRateUs.setTop(worldBounds.getTop());

        btnBack.setLeft(worldBounds.getLeft());
        btnBack.setTop(worldBounds.getTop());

        bg.setPos(worldBounds.getPos());

        btnCloseMenu.setHeightProportion(0.05f);
        btnCloseMenu.setTop(bg.getTop() - 0.11f);
        btnCloseMenu.setRight(bg.getRight());

        final float MENU_BUTTONS_HEIGHT = 0.08f;
        final float MARGIN = 0.09f;

        btnSurvival.setHeightProportion(MENU_BUTTONS_HEIGHT);
        btnSurvival.setPos(bg.getPos().x, bg.getPos().y + 0.023f);
        btnArcade.setHeightProportion(MENU_BUTTONS_HEIGHT);
        btnArcade.setPos(bg.getPos().x, btnSurvival.getPos().y - MARGIN);
        btnPuzzle.setHeightProportion(MENU_BUTTONS_HEIGHT);
        btnPuzzle.setPos(bg.getPos().x, btnArcade.getPos().y - MARGIN);

    }

    @SuppressWarnings("unused")
    @Override
    protected void touchDown(Vector2 touch, long eventTime) {
        btnBack.touchDown(touch);
        switch (state) {
            case MAIN:
                river.touchDown(touch, eventTime);
                btnMenu.touchDown(touch);
                bntHelp.touchDown(touch);
                btnSettings.touchDown(touch);
                btnRateUs.touchDown(touch);
                break;
            case MENU:
                btnCloseMenu.touchDown(touch);
                btnArcade.touchDown(touch);
                btnSurvival.touchDown(touch);
                break;
            case SETTINGS:
                dialogSettings.touchDown(touch);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchMove(Vector2 touch, long eventTime) {
        switch (state) {
            case MAIN:
                river.touchMove(touch, eventTime);
                break;
            case MENU:
            case SETTINGS:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void touchUp(Vector2 touch, long eventTime) {
        btnBack.touchUp(touch);
        switch (state) {
            case MAIN:
                river.touchUp(touch, eventTime);
                btnMenu.touchUp(touch);
                bntHelp.touchUp(touch);
                btnSettings.touchUp(touch);
                btnRateUs.touchUp(touch);
                break;
            case MENU:
                btnCloseMenu.touchUp(touch);
                btnArcade.touchUp(touch);
                btnSurvival.touchUp(touch);
                break;
            case SETTINGS:
                dialogSettings.touchUp(touch);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        draw();
    }

    private void update(float delta) {
        river.update(delta);
    }

    int getRiverPage() {
        return river.getPage();
    }

    void listRiverToPage(int page) {
        river.listToPage(page);
    }

    float getRiverBottomPosY() {
        return river.getBottomPosY();
    }

    void setRiverFromSave(int page, float bottomPosY) {
        river.setFromSave(page, bottomPosY);
    }

    private void draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        river.draw(batch);
        btnRateUs.draw(batch);
        btnMenu.draw(batch);
        bntHelp.draw(batch);
        btnSettings.draw(batch);
        btnBack.draw(batch);
        switch (state) {
            case MAIN:
                break;
            case MENU:
                bg.draw(batch);
                btnCloseMenu.draw(batch);
                btnSurvival.draw(batch);
                btnArcade.draw(batch);
                btnPuzzle.draw(batch);
                break;
            case SETTINGS:
                dialogSettings.draw(batch);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        batch.end();
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            backProcess();
        }
        return false;
    }

    private void backProcess() {
        switch (state) {
            case MAIN:
                Gdx.app.exit();
                break;
            case MENU:
            case SETTINGS:
                state = State.MAIN;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public void onSelectStage(int stage) {
        game.setScreen(new ArcadeGameScreen(game, stage));
    }

    @SuppressWarnings("unused")
    @Override
    public void pause() {
        loader.save();
    }

    @SuppressWarnings("unused")
    @Override
    public void hide() {
        loader.save();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void actionPerformed(Object src) {
        if (src == btnMenu) {
            state = State.MENU;
        } else if (src == btnCloseMenu) {
            state = State.MAIN;
        } else if (src == btnRateUs) {
//            game.rateApp("");
        } else if (src == btnSurvival) {
            game.setScreen(new SurvivalGameScreen(game));
        } else if (src == btnArcade) {
            state = State.MAIN;
        } else if (src == bntHelp) {
            game.setScreen(new HelpScreen(game));
        } else if (src == btnBack) {
            backProcess();
        } else if (src == btnSettings) {
            state = State.SETTINGS;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onDialogSettingsChangeMusic(boolean isMusicOn) {
        game.setMusicOn(isMusicOn);
    }

    @Override
    public void onDialogSettingsChangeSound(boolean isSoundOn) {
        game.setSoundOn(isSoundOn);
    }

    @Override
    public void onDialogSettingsClose() {
        state = State.MAIN;
    }
}