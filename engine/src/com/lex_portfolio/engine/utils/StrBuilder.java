package com.lex_portfolio.engine.utils;

import com.badlogic.gdx.utils.StringBuilder;

public class StrBuilder extends StringBuilder {

    public StrBuilder() {
    }

    public StrBuilder(int capacity) {
        super(capacity);
    }

    public StrBuilder(String string) {
        super(string);
    }

    public StringBuilder clean() {
        clear();
        return this;
    }
}
