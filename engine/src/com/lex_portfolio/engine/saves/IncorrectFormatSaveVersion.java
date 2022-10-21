package com.lex_portfolio.engine.saves;

public class IncorrectFormatSaveVersion extends RuntimeException {

    public IncorrectFormatSaveVersion(String value, int actualVersion) {
        super("Incorrect format version string: '" + value + "' actualVersion = " + actualVersion);
    }
}
