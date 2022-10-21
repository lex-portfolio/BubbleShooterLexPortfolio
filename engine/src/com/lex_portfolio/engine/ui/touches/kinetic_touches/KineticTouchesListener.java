package com.lex_portfolio.engine.ui.touches.kinetic_touches;

import com.badlogic.gdx.math.Vector2;

public interface KineticTouchesListener {

    @SuppressWarnings({"EmptyMethod", "unused"})
    void onKineticBypassClick(Vector2 touch);

    void onKineticFirstMove(Vector2 touch);
    void onKineticFollowingMoves(Vector2 touch);
    void onKineticChangeVelocity(Vector2 v);
}
