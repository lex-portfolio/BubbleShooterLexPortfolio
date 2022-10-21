package com.lex_portfolio.engine.ui.touches;

import com.badlogic.gdx.math.Vector2;

public class TouchesBuffer {

    private static final int DEFAULT_CAPACITY = 100;

    private final TouchMsg[] touches;
    private int length;

    public TouchesBuffer() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("SameParameterValue")
    private TouchesBuffer(int capacity) {
        touches = new TouchMsg[capacity];
        for (int i = 0; i < touches.length; i++) touches[i] = new TouchMsg();
    }

    public void addDown(Vector2 touch, long eventTime) {
        length = 0;
        add(TouchMsg.Type.DOWN, touch, eventTime);
    }

    public void addMove(Vector2 touch, long eventTime) {
        add(TouchMsg.Type.MOVE, touch, eventTime);
    }

    public void addUp(Vector2 touch, long eventTime) {
        add(TouchMsg.Type.UP, touch, eventTime);
    }

    private void add(TouchMsg.Type type, Vector2 pos, long eventTime) {
        if (length < touches.length) length++;
        TouchMsg touch = touches[touches.length - 1];
        System.arraycopy(touches, 0, touches, 1, touches.length - 1);
        touch.set(type, pos, eventTime);
        touches[0] = touch;
    }

    public TouchMsg get(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException("i: " + i);
        return touches[i];
    }

    public TouchMsg getLastTouch() {
        if (length == 0) throw new IllegalStateException("length: 0");
        return touches[0];
    }

    public int getLength() {
        return length;
    }

    public int findOlderTouch(float periodSec) {
        long periodNano = (long) (periodSec * 1e9f);
        long oldTimeThresholdNano = touches[0].getEventTimeNano() - periodNano;
        for (int i = 1; i < length; i++) {
            if (touches[i].getEventTimeNano() < oldTimeThresholdNano) return i - 1;
        }
        return length - 1;
    }
}
