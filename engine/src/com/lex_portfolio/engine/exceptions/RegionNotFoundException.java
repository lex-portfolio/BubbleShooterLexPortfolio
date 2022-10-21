package com.lex_portfolio.engine.exceptions;

public class RegionNotFoundException extends RuntimeException {

    public RegionNotFoundException(String name) {
        super("Can't find region '" + name + "'");
    }
}
