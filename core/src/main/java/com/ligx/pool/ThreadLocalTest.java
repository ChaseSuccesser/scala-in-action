package com.ligx.pool;

import java.util.Random;

/**
 * Author: ligongxing.
 * Date: 2018年01月12日.
 */
public class ThreadLocalTest {

    private static ThreadLocal<Integer> tl = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            Random random = new Random();
            return random.nextInt(1000);
        }
    };


    public static int get() {
        return tl.get();
    }

    public static void set(int value) {
        tl.set(value);
    }
}
