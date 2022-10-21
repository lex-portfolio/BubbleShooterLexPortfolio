package com.lex_portfolio.engine.ui.touches.kinetic_touches;

import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.ui.touches.TouchMsg;
import com.lex_portfolio.engine.ui.touches.TouchesBuffer;

public class KineticTouches {

    private static final float DEFAULT_AVERAGE_PERIOD = 0.3f;
    private static final float DEFAULT_THRESHOLD = 0.02f;
    private static final float DEFAULT_BREAK_ACCELERATE = 2f;

    private final KineticTouchesListener listener;
    private final float averagePeriod;
    private final float threshold;
    private final float breakAccelerate;
    private final TouchesBuffer touches = new TouchesBuffer();
    private final Vector2 d = new Vector2();
    private boolean isMoving;
    private final Vector2 lastTouchDown = new Vector2();

    @SuppressWarnings("unused")
    public KineticTouches(KineticTouchesListener listener) {
        this(listener, DEFAULT_THRESHOLD, DEFAULT_AVERAGE_PERIOD, DEFAULT_BREAK_ACCELERATE);
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private KineticTouches(KineticTouchesListener listener, float threshold, float averagePeriod, float breakAccelerate) {
        this.listener = listener;
        this.threshold = threshold;
        this.averagePeriod = averagePeriod;
        this.breakAccelerate = breakAccelerate;
    }

    public void touchDown(Vector2 touch, long eventTime) {
        lastTouchDown.set(touch);
        touches.addDown(touch, eventTime);
        stop();
        isMoving = false;
    }

    public void touchMove(Vector2 touch, long eventTime) {
        touches.addMove(touch, eventTime);
        if (isMoving) {
            listener.onKineticFollowingMoves(touch);
        } else {
            float len = d.set(touch).sub(lastTouchDown).len();
            if (len > threshold) {
                isMoving = true;
                listener.onKineticFirstMove(touch);
            }
        }
    }

    public void touchUp(Vector2 touch, long eventTime) {
        touches.addUp(touch, eventTime);
        if (isMoving) {
            updateAverageV(averagePeriod);
        } else {
            listener.onKineticBypassClick(touch);
        }
    }

    public TouchMsg getLastTouch() {
        return touches.getLastTouch();
    }

    private final Vector2 s = new Vector2();
    @SuppressWarnings("SpellCheckingInspection")
    private final Vector2 Vnorm = new Vector2();
    @SuppressWarnings("SpellCheckingInspection")
    private float Vabs;

    private void setZeroV() {
        Vnorm.setZero();
        Vabs = 0f;
    }

    public void stop() {
        setZeroV();
        sendChangeSpeedToListener();
    }

    private void updateAverageV(float periodSec) {
        if (touches.getLength() == 0) throw new IllegalStateException("touches.getLength() == 0");
        int f = touches.findOlderTouch(periodSec);
        if (f == 0) {
            setZeroV();
        } else {
            TouchMsg touchOld = touches.get(f);
            TouchMsg touchYoung = touches.getLastTouch();
            s.set(touchYoung.getPos()).sub(touchOld.getPos());
            float deltaTimeSec = (touchYoung.getEventTimeNano() - touchOld.getEventTimeNano()) * 1e-9f;
            if (deltaTimeSec == 0f) {
                setZeroV();
            } else {
                Vabs = Vnorm.set(s).scl(1f / deltaTimeSec).len();
                Vnorm.nor();
            }
        }
        sendChangeSpeedToListener();
    }

    public void update(float deltaTime) {
        if (Vabs > 0f) {
            Vabs -= breakAccelerate * deltaTime;
            if (Vabs < 0f) {
                setZeroV();
            }
            sendChangeSpeedToListener();
        }
    }

    private final Vector2 out = new Vector2();

    private void sendChangeSpeedToListener() {
        listener.onKineticChangeVelocity(out.set(Vnorm).scl(Vabs));
    }
}