package com.lex_portfolio.engine.utils;

import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TextFile {

    public static List<String> readTextFromFile(FileHandle file) throws IOException {
        List<String> list = new ArrayList<>();
        readTextFromFile(file, list);
        return list;
    }

    public static void readTextFromFile(FileHandle file, List<String> strings) throws IOException {
        readTextFromFile(file.read(), strings);
    }

    @SuppressWarnings("CharsetObjectCanBeUsed")
    public static void readTextFromFile(InputStream in, List<String> strings) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String s;
        while ((s = br.readLine()) != null) strings.add(s);
        br.close();
    }

    public static void writeTextToFile(FileHandle file, List<String> strings) throws IOException {
        writeTextToFile(file.write(false), strings);
    }

    @SuppressWarnings("CharsetObjectCanBeUsed")
    public static void writeTextToFile(OutputStream out, List<String> strings) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        for (int i = 0, cnt = strings.size(); i < cnt; i++) {
            bw.write(strings.get(i));
            if (i != cnt - 1) bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
