package com.lex_portfolio.bubble_shooter.dialogs.settings;

public interface DialogSettingsListener {
    void onDialogSettingsChangeMusic(boolean isMusicOn);
    void onDialogSettingsChangeSound(boolean isSoundOn);
    void onDialogSettingsClose();
}