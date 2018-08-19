package com.ligx.id;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by ligongxing on 2016/11/10.
 */
public class JUUID {

    /**
     * 时间戳（48位）
     */
    private byte[] getTimeSignificant(){
        byte[] bytes = new byte[6];
        long seed = System.currentTimeMillis();
        short _16bitTime = (short) (seed >> 32);
        int _32bitTime = (int) seed;
        System.arraycopy(IdHelper.fromShort(_16bitTime), 0, bytes, 0, 2);
        System.arraycopy(IdHelper.fromInt(_32bitTime), 0, bytes, 2, 4);
        return bytes;
    }

    /**
     * 顺序号（16位）：原子递增，short溢出到了负数就归0重头开始
     */
    private short counter = (short)0;
    private short getShortCount(){
        synchronized (JUUID.class){
            if(counter < 0){
                counter = 0;
            }
            return counter;
        }
    }

    /**
     * 机器标识（32位）：拿localHost的IP地址，IPV4正好4个byte
     */
    private byte[] getAddressByte() {
        byte[] address;
        try {
            address = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            address = new byte[4];
        }
        return address;
    }

    /**
     * 进程标识（32位）：当前时间戳右移8位再取整数
     */
    private byte[] getJvmIdentifierBytes() {
        int JVM_IDENTIFIER_INT = (int) (System.currentTimeMillis() >>> 8);
        return IdHelper.fromInt(JVM_IDENTIFIER_INT);
    }

    private long generateMostSignificantBits(){
        byte[] bytes = new byte[8];
        System.arraycopy(getAddressByte(), 0, bytes, 0, 4);
        System.arraycopy(getJvmIdentifierBytes(), 0, bytes, 4, 4);
        return IdHelper.toLong(bytes);
    }

    private long generateLeastSignificantBits() {
        byte[] bytes = new byte[8];
        System.arraycopy(getTimeSignificant(), 0, bytes, 0, 6);
        System.arraycopy(IdHelper.fromShort(getShortCount()), 0, bytes, 6, 2);
        return IdHelper.toLong(bytes);
    }

    public UUID generateUUID() {
        long mostSignificantBits = generateMostSignificantBits();
        long leastSignificantBits = generateLeastSignificantBits();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    /*
    c0a80169-584f-3293-0158-4f3293060000
    c0a80169-584f-3465-0158-4f3465620000
    c0a80169-584f-34ac-0158-4f34ac720000
     */
    public static void main(String[] args) {
        System.err.println(new JUUID().generateUUID());
    }
}
