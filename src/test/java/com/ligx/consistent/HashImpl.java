package com.ligx.consistent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: ligongxing.
 * Date: 2017年08月24日.
 */
public class HashImpl implements IHash {

    @Override
    public long hash(String key) {
        MessageDigest instance = null;
        try {
            instance = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        instance.reset();
        instance.update(key.getBytes());
        byte[] digest = instance.digest();

        long h = 0;
        for (int i = 0; i < 4; i++) {
            h <<= 8;
            h |= ((int) digest[i]) & 0xFF;
        }
        return h;
    }
}
