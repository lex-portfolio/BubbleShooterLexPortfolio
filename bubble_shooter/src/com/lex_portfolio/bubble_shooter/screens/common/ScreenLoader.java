package com.lex_portfolio.bubble_shooter.screens.common;

import com.lex_portfolio.bubble_shooter.balls.Ball;
import com.lex_portfolio.engine.game_elements.spinner.Spinner;
import com.lex_portfolio.engine.utils.Str;

import java.util.List;

@SuppressWarnings({"ForLoopReplaceableByForEach", "SpellCheckingInspection"})
public class ScreenLoader<T extends GameScreen> extends GridLoader<T> {

    private final Spinner spinner;

    public ScreenLoader(T screen) {
        super(screen, screen.getPoolBalls());
        this.spinner = screen.getSpinner();
    }

    protected void saveMajorBall(List<String> save, Ball ball) {
        if (ball == null) {
            save.add("FlyingMajorBall state=\"NULL\"");
        } else {
            Ball.Type type = ball.getType();
            save.add("FlyingMajorBall state=\"FLYING\" " + ball.getStringPos() + " " + ball.getStringV() + " type=\"" + type + "\"");
        }
    }

    protected int parseFlyingMajorBall(List<String> save, int saveLineIndex) {
        String line = save.get(saveLineIndex);
        String state = Str.parseStr(line, "state=");
        if (state.equals("FLYING")) {
            float posx = Str.parseFloat(line, "pos.x=");
            float posy = Str.parseFloat(line, "pos.y=");
            float vx = Str.parseFloat(line, "v.x=");
            float vy = Str.parseFloat(line, "v.y=");
            Ball.Type type = Ball.Type.valueOf(Str.parseStr(line, "type="));
            Ball majorBall = poolBalls.obtain(type);
            majorBall.setMajorFromSave(posx, posy, vx, vy);
            screen.setFlyingMajorBallFromSave(majorBall);
        }
        return saveLineIndex + 1;
    }

    protected void saveBallsCartridge(List<String> save) {
        save.add("BeginCartridge");
        Ball[] cartridge = screen.getCartridge();
        for (int i = 0; i < cartridge.length; i++) {
            Ball ball = cartridge[i];
            if (ball == null) {
                save.add("\tBall type=\"NULL\"");
                continue;
            }
            save.add("\tBall type=\"" + ball.getType() + "\"");
        }
        save.add("EndCartridge");
    }

    private final Ball[] cartridge = new Ball[3];


    @SuppressWarnings("ExplicitArrayFilling")
    protected int parseBallCartridge(List<String> save, int saveLineIndex) {
        saveLineIndex++;
        for (int i = 0; i < cartridge.length; i++) {
            String line = save.get(saveLineIndex + i);
            String strType = Str.parseStr(line, "type=");
            if (strType.equals("NULL")) {
                cartridge[i] = null;
            } else {
                cartridge[i] = poolBalls.obtain(Ball.Type.valueOf(strType));
            }
        }
        screen.setCartridgeFromSave(cartridge);
        for (int i = 0; i < cartridge.length; i++) {
            cartridge[i] = null;
        }
        return saveLineIndex + 4;
    }

    protected void saveSpinner(List<String> save) {
        String line =
                "Spinner state=\"" + spinner.getState() +
                        "\" upper_index=" + spinner.getUpperIndex() +
                        " angle=" + spinner.getAngle();
        save.add(line);
    }

    protected void parseSpinner(List<String> save, int lineIndex) {
        String line = save.get(lineIndex);
        Spinner spinner = screen.getSpinner();
        int upperIndex = Str.parseInt(line, "upper_index=");
        Spinner.State state = Spinner.State.valueOf(Str.parseStr(line, "state="));
        switch (state) {
            case IDLE:
                spinner.setIdle(upperIndex);
                break;
            case SHORT_ROTATE:
                float angle = Str.parseFloat(line, "angle=");
                spinner.setShortRotate(upperIndex, angle);
                break;
            case LONG_ROTATE:
                angle = Str.parseFloat(line, "angle=");
                spinner.setLongRotate(upperIndex, angle);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    protected void saveBonuses(List<String> save) {
        save.add("Bonuses fireball=" + screen.getFireballProgress() + " bomb=" + screen.getBombProgress());
    }

    @SuppressWarnings("SameParameterValue")
    protected void parseBonuses(List<String> save, int lineIndex) {
        String line = save.get(lineIndex);
        screen.setFireballProgress(Str.parseFloat(line, "fireball="));
        screen.setBombProgress(Str.parseFloat(line, "bomb="));
    }
}
