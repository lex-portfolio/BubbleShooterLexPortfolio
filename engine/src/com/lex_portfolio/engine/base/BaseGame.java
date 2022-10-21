package com.lex_portfolio.engine.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lex_portfolio.engine.android.AndroidInterface;
import com.lex_portfolio.engine.android.Orientation;
import com.lex_portfolio.engine.saves.Saves;

import java.util.HashMap;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class BaseGame implements ApplicationListener {

    private final AndroidInterface androidInterface;

    private Base2DScreen<? extends BaseGame> screen;
    private SpriteBatch batch;
    private final Saves saves = new Saves();

    private final HashMap<String, Sound> sounds = new HashMap<>();
    private final HashMap<String, Music> musics = new HashMap<>();
    private final HashMap<String, BaseTextureAtlas> atlases = new HashMap<>();
    private final HashMap<String, Sprite2DTexture> textures = new HashMap<>();
    private final HashMap<String, Font> fonts = new HashMap<>();

    protected BaseGame(AndroidInterface androidInterface) {
        this.androidInterface = androidInterface;
    }

    public BaseTextureAtlas getAtlas(String name) {
        BaseTextureAtlas atlas = atlases.get(name);
        if (atlas == null) throw new RuntimeException("Atlas " + name + " not found.");
        return atlas;
    }

    @SuppressWarnings("unused")
    public Sprite2DTexture getTexture(String name) {
        Sprite2DTexture texture = textures.get(name);
        if (texture == null) throw new RuntimeException("Texture " + name + " not found.");
        return texture;
    }

    public Sound getSound(String name) {
        Sound sound = sounds.get(name);
        if (sound == null) throw new RuntimeException("Sound " + name + " not found.");
        return sound;
    }

    public Music getMusic(String name) {
        Music music = musics.get(name);
        if (music == null) throw new RuntimeException("Music " + name + " not found.");
        return music;
    }

    public Font getFont(String name) {
        Font font = fonts.get(name);
        if (font == null) throw new RuntimeException("Font " + name + " not found.");
        return font;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Saves getSaves() {
        return saves;
    }

    public boolean isAndroid() {
        return androidInterface != null;
    }

    @SuppressWarnings("unused")
    public void rateApp(String packageName) {
        if (isAndroid()) androidInterface.openPlayMarketPage(packageName);
    }

    public void toastLong(String msg) {
        if (isAndroid()) androidInterface.toast(msg, AndroidInterface.Duration.LONG);
    }

    @SuppressWarnings("unused")
    public void setOrientation(Orientation orientation) {
        if (isAndroid()) androidInterface.setOrientation(orientation);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        FileHandle dir = Gdx.files.internal("sounds");
        if (dir.exists()) {
            FileHandle[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                FileHandle file = files[i];
                sounds.put(file.name(), Gdx.audio.newSound(file));
            }
        }

        dir = Gdx.files.internal("musics");
        if (dir.exists()) {
            FileHandle[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                FileHandle file = files[i];
                musics.put(file.name(), Gdx.audio.newMusic(file));
            }
        }

        dir = Gdx.files.internal("textures");
        if (dir.exists()) {
            FileHandle[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                FileHandle file = files[i];
                textures.put(file.name(), new Sprite2DTexture(file));
            }
        }

        dir = Gdx.files.internal("atlases");
        if (dir.exists()) {
            FileHandle[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                FileHandle file = files[i];
                if (file.extension().equals("atlas"))
                    atlases.put(file.name(), new BaseTextureAtlas(file));
            }
        }

        dir = Gdx.files.internal("fonts");
        if (dir.exists()) {
            FileHandle[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                FileHandle file = files[i];
                if (file.extension().equals("fnt")) {
                    fonts.put(file.name(), new Font(file.path(), file.pathWithoutExtension() + ".png"));
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        screen.resize(width, height);
    }

    @Override
    public void pause() {
        screen.pause();
    }

    @Override
    public void resume() {
        screen.resume();
    }

    @Override
    public void dispose() {
        screen.hide();
        for (Sound sound : sounds.values()) sound.dispose();
        for (Music music : musics.values()) music.dispose();
        for (BaseTextureAtlas atlas : atlases.values()) atlas.dispose();
        for (Sprite2DTexture texture : textures.values()) texture.dispose();
        for (Font font : fonts.values()) font.dispose();
        batch.dispose();
    }

    public void setScreen(Base2DScreen<? extends BaseGame> newScreen) {
        if (screen != null) screen.hide();
        newScreen.show();
        newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screen = newScreen;
    }

    @Override
    public void render() {
        screen.render(Gdx.graphics.getDeltaTime());
    }
}
