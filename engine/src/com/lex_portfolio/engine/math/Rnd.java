package com.lex_portfolio.engine.math;

import java.util.Random;

public class Rnd {

    private static final Random rnd = new Random();

    public static float nextFloat(float min, float max) {
        return rnd.nextFloat() * (max - min) + min;
    }

    @SuppressWarnings("unused")
    public static int nextInt (int n){
        return rnd.nextInt(n);
    }
}
