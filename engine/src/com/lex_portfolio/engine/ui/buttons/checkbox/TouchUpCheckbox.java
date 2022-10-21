package com.lex_portfolio.engine.ui.buttons.checkbox;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.lex_portfolio.engine.sprites.Sprite;

public class TouchUpCheckbox extends Sprite implements Checkbox {

    private boolean pressed;
    private final CheckboxListener listener;

    public TouchUpCheckbox(TextureRegion region, CheckboxListener listener) {
        super(region, 1, 2);
        this.listener = listener;
    }

    public void touchDown(Vector2 touch) {
        if (isMe(touch)) {
            pressed = true;
        }
    }

    public void touchUp(Vector2 touch) {
        if (pressed) {
            pressed = false;
            if (isMe(touch)) {
                frame = ~frame & 0x1;
                listener.onChangeCheckbox(this);
            }
        }
    }

    public void setChecked(boolean checked) {
        frame = checked ? 1 : 0;
    }

    @Override
    public boolean isChecked() {
        return frame == 1;
    }
}
