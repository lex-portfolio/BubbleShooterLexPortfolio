package com.lex_portfolio.engine.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Drawer {

    public static void draw(SpriteBatch batch, ArrayList<? extends Drawable> objects) {
        for (int i = 0, cnt = objects.size(); i < cnt; i++) {
            objects.get(i).draw(batch);
        }
    }
}
