package com.ligx.pool;

import java.util.Random;

/**
 * Author: ligongxing.
 * Date: 2018年01月12日.
 */
public class InheritableThreadLocalTest {

    private static InheritableThreadLocal<Integer> itl = new InheritableThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            Random random = new Random();
            return random.nextInt(1000);
        }

        @Override
        protected Integer childValue(Integer parentValue) {
            return parentValue + 1;
        }
    };


    public static int get() {
        return itl.get();
    }


    public static void set(int value) {
        itl.set(value);
    }
}
