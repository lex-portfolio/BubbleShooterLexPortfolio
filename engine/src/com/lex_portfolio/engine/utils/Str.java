package com.lex_portfolio.engine.utils;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class Str {

    public static int parseInt(String s, String prefix) {
        return Integer.parseInt(getSpaceSubstring(s, prefix));
    }

    public static float parseFloat(String s, String prefix) {
        return Float.parseFloat(getSpaceSubstring(s, prefix));
    }

    public static boolean parseBool(String s, String prefix) {
        return Boolean.parseBoolean(getSpaceSubstring(s, prefix));
    }

    public static String parseStr(String s, String prefix) {
        prefix += "\"";
        String[] tokens = s.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith(prefix)) {
                return token.substring(prefix.length(), token.length() - 1);
            }
        }
        throw new RuntimeException("'" + prefix + "' not found in string '" + s + "'");
    }


    private static String getSpaceSubstring(String s, String prefix) {
        String[] tokens = s.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith(prefix)) {
                return token.substring(prefix.length());
            }
        }
        throw new RuntimeException("'" + prefix + "' not found in string '" + s + "'");
    }
}
