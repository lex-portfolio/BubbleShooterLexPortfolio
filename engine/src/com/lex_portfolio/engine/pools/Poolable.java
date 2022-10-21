package com.lex_portfolio.engine.pools;

@SuppressWarnings("SpellCheckingInspection")
public interface Poolable {
    void onObtainFromPool();
    @SuppressWarnings("EmptyMethod")
    void onReturnToPool();
}
