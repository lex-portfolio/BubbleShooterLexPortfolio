package com.lex_portfolio.bubble_shooter.sounds;

import com.badlogic.gdx.audio.Sound;
import com.lex_portfolio.bubble_shooter.GameBubbleShooter;
import com.lex_portfolio.bubble_shooter.settings.Settings;

public class Sounds {

    private final Settings settings;
    private final Sound shoot;
    private final Sound downfall;
    private final Sound collision;
    private final Sound explosion;
    private final Sound bonusReady;

    public enum Type {
        SHOOT_COLORED, SHOOT_IRON, SHOOT_FIREBALL, SHOOT_BOMB, EXPLOSION_COLOR, EXPLOSION_FIREBALL,
        EXPLOSION_BOMB, COLLISION_COLOR, COLLISION_IRON, COLLISION_BOMB, DOWNFALL, BONUS_READY
    }

    public Sounds(GameBubbleShooter game, Settings settings) {
        this.settings = settings;
        shoot = game.getSound("shoot.wav");
        collision = game.getSound("collision.wav");
        downfall = game.getSound("downfall.wav");
        explosion = game.getSound("explosion.wav");
        bonusReady = game.getSound("bonus_ready.wav");
    }

    public void play(Type type) {
        if (!settings.isSoundOn()) return;
        switch (type) {
            case SHOOT_COLORED:
            case SHOOT_IRON:
            case SHOOT_FIREBALL:
            case SHOOT_BOMB:
                shoot.play(1f);
                break;
            case EXPLOSION_COLOR:
            case EXPLOSION_FIREBALL:
            case EXPLOSION_BOMB:
                explosion.play(0.6f);
                break;
            case COLLISION_COLOR:
            case COLLISION_IRON:
            case COLLISION_BOMB:
                collision.play(0.2f);
                break;
            case DOWNFALL:
                downfall.play(0.3f);
                break;
            case BONUS_READY:
                bonusReady.play(0.3f);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
