package com.lex_portfolio.engine.ui.touches;

import com.badlogic.gdx.math.Vector2;

public class TouchMsg {

    enum Type {DOWN, MOVE, UP}

    private Type type;
    private final Vector2 pos = new Vector2();
    private long eventTimeNano;

    public void set(Type type, Vector2 pos, long eventTimeNano) {
        this.type = type;
        this.pos.set(pos);
        this.eventTimeNano = eventTimeNano;
    }

    @SuppressWarnings("unused")
    public Type getType() {
        return type;
    }

    public Vector2 getPos() {
        return pos;
    }

    public long getEventTimeNano() {
        return eventTimeNano;
    }
}
