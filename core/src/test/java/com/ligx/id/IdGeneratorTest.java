package com.ligx.id;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class IdGeneratorTest {

    @Test
    public void get() {
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            long id = IdGenerator.get();
            System.out.println(id + "--->" + IdGenerator.parseId(id));
            set.add(id);
        }
        System.out.println(set.size());
    }
}