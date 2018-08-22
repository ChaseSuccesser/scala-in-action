package com.ligx.id;

import com.ligx.utils.NetUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);

    /**
     * 每一部分占用的位数
     */
    private static final long TIME_STAMP_BIT = 42;
    private static final long NODE_BIT = 10;
    private static final long SEQUENCE_BIT = 12;

    /**
     * 每一部分的最大值
     */
    private static final long MAX_SEQUENCE_NUM = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private static final long NODE_LEFT = SEQUENCE_BIT;
    private static final long TIMESTAMP_LEFT = NODE_BIT + SEQUENCE_BIT;

    private long node;            //机器节点标识
    private long sequence = 0L;   //序列号
    private long lastStamp = -1L; //上一次时间戳

    private static IdGenerator instance = new IdGenerator();

    private IdGenerator() {
        long ip = NetUtil.ipToLong(NetUtil.getIp());
        this.node = ip & 0x3FFL;
    }


    public static long get() {
        long id = 0L;
        try {
            id = IdGenerator.instance.nextId();
        } catch (Exception e) {
            LOGGER.error("IdGenerator->get:error", e);
            System.err.println(e.getMessage());
        }
        if (id == 0L) {
            try {
                Thread.sleep(3L);
                id = IdGenerator.instance.nextId();
            } catch (Exception e) {
                LOGGER.error("IdGenerator#get:error", e);
                System.err.println(e.getMessage());
            }
        }
        if (id == 0L) {
            return System.currentTimeMillis() + (int) (Math.random() * 10000);
        }
        return id;
    }


    /**
     * 产生下一个ID
     *
     * @return
     */
    private synchronized long nextId() {
        long currStamp = getCurrStamp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStamp == lastStamp) {
            // 相同毫秒内，序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE_NUM;
            // 同一毫秒内，序列号已经达到最大，切到下一毫秒
            if (sequence == 0L) {
                currStamp = getNextMills();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStamp = currStamp;

        return (lastStamp) << TIMESTAMP_LEFT
                | node << NODE_LEFT
                | sequence;
    }


    private long getNextMills() {
        long mill = getCurrStamp();
        while (mill <= lastStamp) {
            mill = getCurrStamp();
        }
        return mill;
    }

    private long getCurrStamp() {
        return System.currentTimeMillis();
    }


    /**
     * 反解出id中的各个部分值
     *
     * @param id
     * @return
     */
    public static String parseId(long id) {
        long sequenceNum = (id << (64 - SEQUENCE_BIT)) >> (64 - SEQUENCE_BIT);
        long nodeNum = (id << (64 - NODE_BIT - SEQUENCE_BIT)) >> (64 - NODE_BIT);
        long timeStamp = id >> (64 - TIME_STAMP_BIT);

        String time = new DateTime(timeStamp).toString("yyyy-MM-dd HH:mm:ss:SSS");

        return String.format("time:%s, nodeNum:%s, sequence:%s", time, nodeNum, sequenceNum);

    }
}
