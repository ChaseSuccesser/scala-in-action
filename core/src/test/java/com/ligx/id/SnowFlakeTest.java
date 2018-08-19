package com.ligx.id;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Created by li.gongxing on 2018/05/30.
 */
public class SnowFlakeTest {

  @Test
  public void nextIdTest() {
    SnowFlake snowFlake = new SnowFlake(2, 3);
    Set<Long> set = new HashSet<>();
    for (int i = 0; i < 10000; i++) {
      long id = snowFlake.nextId();
      System.out.println(id + ",,,,," + SnowFlake.parseId(id));
      set.add(id);
    }
    System.out.println(set.size());
  }
}
