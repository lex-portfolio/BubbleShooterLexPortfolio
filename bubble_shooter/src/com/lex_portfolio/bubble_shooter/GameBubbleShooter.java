package com.lex_portfolio.bubble_shooter;

import com.badlogic.gdx.audio.Music;
import com.lex_portfolio.bubble_shooter.balls.PoolBalls;
import com.lex_portfolio.bubble_shooter.screens.ScreenManager;
import com.lex_portfolio.bubble_shooter.settings.Settings;
import com.lex_portfolio.bubble_shooter.sounds.Sounds;
import com.lex_portfolio.bubble_shooter.statistics.Statistics;
import com.lex_portfolio.engine.android.AndroidInterface;
import com.lex_portfolio.engine.base.BaseGame;

public class GameBubbleShooter extends BaseGame {

    private ScreenManager screenManager;
    private Statistics statistics;
    private Settings settings;
    private Sounds sounds;
    private PoolBalls pollBalls;
    private Music music;

    public GameBubbleShooter(AndroidInterface androidInterface) {
        super(androidInterface);
    }

    @Override
    public void create() {
        super.create();
        screenManager = new ScreenManager(this);
        statistics = new Statistics(this);
        settings = new Settings(this);
        sounds = new Sounds(this, settings);
        pollBalls = new PoolBalls(getAtlas("game.atlas"));

        music = getMusic("bg.mp3");
        music.setLooping(true);
        music.setVolume(1f);
        if (settings.isMusicOn()) music.play();

        setScreen(screenManager.getLastScreen());
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Settings getSettings() {
        return settings;
    }

    public Sounds getSounds() {
        return sounds;
    }

    public PoolBalls getPoolBalls() {
        return pollBalls;
    }

    public void setMusicOn(boolean isMusicOn) {
        settings.setMusicOn(isMusicOn);
        if (isMusicOn) {
            music.play();
        } else {
            music.pause();
        }
    }

    public void setSoundOn(boolean isSoundOn) {
        settings.setSoundOn(isSoundOn);
    }

    @Override
    public void resume() {
        super.resume();
        if (settings.isMusicOn()) music.play();
    }

    @Override
    public void pause() {
        music.pause();
        statistics.save();
        settings.save();
        super.pause();
    }

    @Override
    public void dispose() {
        pollBalls.dispose();
        super.dispose();
    }
}
