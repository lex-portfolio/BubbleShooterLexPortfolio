package com.lex_portfolio.bubble_shooter.dialogs.settings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.lex_portfolio.engine.base.Font;
import com.lex_portfolio.engine.sprites.Sprite;
import com.lex_portfolio.engine.ui.ActionListener;
import com.lex_portfolio.engine.ui.buttons.ScaledTouchUpButton;
import com.lex_portfolio.engine.ui.buttons.checkbox.Checkbox;
import com.lex_portfolio.engine.ui.buttons.checkbox.CheckboxListener;
import com.lex_portfolio.engine.ui.buttons.checkbox.TouchUpCheckbox;
import com.lex_portfolio.engine.utils.StrBuilder;

public class DialogSettings implements CheckboxListener, ActionListener {

    private static final float BG_HEIGHT = 0.5f;
    private static final float BUTTONS_HEIGHT = 0.08f;
    private static final float BUTTON_CLOSE_HEIGHT = 0.05f;
    private static final float FONT_SIZE = 0.025f;
    private static final float TEXT_LEFT_MARGIN = 0.06f;

    private final Sprite bg;
    private final ScaledTouchUpButton btnClose;
    private final TouchUpCheckbox chkMusic;
    private final TouchUpCheckbox chkSound;
    private final Font font;
    private final StrBuilder sbMusicOn = new StrBuilder("Music on");
    private final StrBuilder sbMusicOff = new StrBuilder("Music off");
    private final StrBuilder sbSoundOn = new StrBuilder("Sound on");
    private final StrBuilder sbSoundOff = new StrBuilder("Sound off");
    private final DialogSettingsListener listener;

    public DialogSettings(
            TextureRegion regionBg,
            TextureRegion regionBtnMusic,
            TextureRegion regionBtnSound,
            TextureRegion regionBtnClose,
            Font font,
            boolean isMusicOn,
            boolean isSoundOn,
            DialogSettingsListener listener
    ) {
        this.listener = listener;
        this.font = font;
        bg = new Sprite(regionBg);
        bg.setHeightProportion(BG_HEIGHT);
        chkMusic = new TouchUpCheckbox(regionBtnMusic, this);
        chkMusic.setHeightProportion(BUTTONS_HEIGHT);
        chkSound = new TouchUpCheckbox(regionBtnSound, this);
        chkSound.setHeightProportion(BUTTONS_HEIGHT);
        btnClose = new ScaledTouchUpButton(regionBtnClose, this);
        btnClose.setHeightProportion(BUTTON_CLOSE_HEIGHT);
        chkMusic.getPos().set(bg.getPos()).add(-0.5f * bg.getHalfWidth(), 0f * bg.getHalfHeight());
        chkSound.getPos().set(bg.getPos()).add(-0.5f * bg.getHalfWidth(), -0.45f * bg.getHalfHeight());
        btnClose.getPos().set(bg.getPos()).add(0.8f * bg.getHalfWidth(), 0.48f * bg.getHalfHeight());
        chkMusic.setChecked(isMusicOn);
        chkSound.setChecked(isSoundOn);
    }

    public void touchDown(Vector2 touch) {
        chkMusic.touchDown(touch);
        chkSound.touchDown(touch);
        btnClose.touchDown(touch);
    }

    public void touchUp(Vector2 touch) {
        chkMusic.touchUp(touch);
        chkSound.touchUp(touch);
        btnClose.touchUp(touch);
    }

    public void draw(SpriteBatch batch) {
        bg.draw(batch);
        chkMusic.draw(batch);
        chkSound.draw(batch);
        btnClose.draw(batch);

        font.setWorldSize(FONT_SIZE);
        float posX = chkMusic.getPos().x + TEXT_LEFT_MARGIN;
        float posy = chkMusic.getPos().y + font.getCapHeight() * 0.5f;
        if (chkMusic.isChecked()) {
            font.draw(batch, sbMusicOn, posX, posy, Align.left);
        } else {
            font.draw(batch, sbMusicOff, posX, posy, Align.left);
        }
        posy = chkSound.getPos().y + font.getCapHeight() * 0.5f;
        if (chkSound.isChecked()) {
            font.draw(batch, sbSoundOn, posX, posy, Align.left);
        } else {
            font.draw(batch, sbSoundOff, posX, posy, Align.left);
        }
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == btnClose) {
            listener.onDialogSettingsClose();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onChangeCheckbox(Checkbox src) {
        if (src == chkMusic) {
            listener.onDialogSettingsChangeMusic(src.isChecked());
        } else if (src == chkSound) {
            listener.onDialogSettingsChangeSound(src.isChecked());
        } else {
            throw new RuntimeException();
        }
    }
}
