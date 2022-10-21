package com.lex_portfolio.engine.android;

public interface AndroidInterface {

    enum Duration {SHORT, LONG}

    void toast(String msg, Duration duration);
    @SuppressWarnings("unused")
    void setOrientation(Orientation orientation);
    void openPlayMarketPage(String packageName);
}
