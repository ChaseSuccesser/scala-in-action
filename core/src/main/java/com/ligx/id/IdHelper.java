package com.ligx.id;

/**
 * Created by li.gongxing on 2018/05/30.
 */
public class IdHelper {

  /**
   * 将short值的二进制表示，每8位分割，分别放到长度为2的byte数组中
   */
  public static byte[] fromShort(short shortValue) {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) (shortValue >> 8);
    bytes[1] = (byte) ((shortValue << 8) >> 8);
    return bytes;
  }

  /**
   * 将int值的二进制表示，每8位分割，分别放到长度为4的byte数组中
   */
  public static byte[] fromInt(int intValue) {
    byte[] bytes = new byte[4];
    bytes[0] = (byte) (intValue >> 24);
    bytes[1] = (byte) ((intValue << 8) >> 24);
    bytes[2] = (byte) ((intValue << 16) >> 24);
    bytes[3] = (byte) ((intValue << 24) >> 24);
    return bytes;
  }

  /**
   * 将长度为8的byte数组中所有的二进制位拼装在一起，得到一个long值
   */
  public static long toLong(byte[] bytes) {
    if (bytes == null) {
      return 0L;
    }
    if (bytes.length != 8) {
      throw new IllegalArgumentException("Expecting 8 byte values to construct a long");
    }
    long longValue = 0L;
    for (int i = 0; i < 8; i++) {
      longValue = (longValue << 8) | (bytes[i] & 0xff);
    }
    return longValue;
  }
}
