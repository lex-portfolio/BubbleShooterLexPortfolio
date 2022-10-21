package com.lex_portfolio.engine.saves;

import static com.lex_portfolio.engine.utils.TextFile.readTextFromFile;

import com.badlogic.gdx.files.FileHandle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Saves {

    private static final String VERSION = "Version";
    private static final String SEP = "=";
    private final ArrayList<String> save = new ArrayList<>();

    public ArrayList<String> createSave(int version) {
        save.clear();
        save.add(VERSION + SEP + version);
        return save;
    }

    public ArrayList<String> readActualVersionSave(FileHandle file, int actualVersion) {
        save.clear();
        try {
            readTextFromFile(new FileInputStream(file.file()), save);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (save.isEmpty()) return null;
        String first = save.get(0);
        String[] tokens = first.split(SEP);
        if (tokens.length != 2 || !tokens[0].equals(VERSION)) return null;
        int fileVersion;
        try {
            fileVersion = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new IncorrectFormatSaveVersion(first, actualVersion);
        }
        if (fileVersion == actualVersion) {
            return save;
        }
        return null;
    }
}
