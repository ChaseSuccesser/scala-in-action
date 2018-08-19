package com.ligx.id;

import org.joda.time.DateTime;

/**
 * twitter的snowflake算法.
 *
 * 原理
 * SnowFlake算法产生的ID是一个64位的整型，结构如下（每一部分用“-”符号分隔）：
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 *
 * 1. [1位标识部分]，在java中由于long的最高位是符号位，正数是0，负数是1，一般生成的ID为正数，所以为0；
 * 2. [41位时间戳部分]，这个是毫秒级的时间，一般实现上不会存储当前的时间戳，而是时间戳的差值（当前时间-固定的开始时间），
 *    这样可以使产生的ID从更小值开始；41位的时间戳可以使用69年，(1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69年；
 * 3. [10位节点部分]，Twitter实现中使用前5位作为数据中心标识，后5位作为机器标识，可以部署1024个节点；
 * 4. [12位序列号部分]，支持同一毫秒内同一个节点可以生成4096个ID；
 *
 * Created by li.gongxing on 2018/05/30.
 */
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private static final long START_STAMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private static final long TIME_STAMP_BIT = 42;
    private static final long DATA_CENTER_BIT = 5;
    private static final long MACHINE_BIT = 5;
    private static final long SEQUENCE_BIT = 12;

    /**
     * 每一部分的最大值
     */
    private static final long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE_NUM = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATA_CENTER_LEFT = MACHINE_BIT + SEQUENCE_BIT;
    private static final long TIMESTAMP_LEFT = DATA_CENTER_BIT + MACHINE_BIT + SEQUENCE_BIT;

    private long dataCenterId;    //数据中心
    private long machineId;       //机器标识
    private long sequence = 0L;   //序列号
    private long lastStamp = -1L; //上一次时间戳

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
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

        return (lastStamp - START_STAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
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
     * 反解出id中的4个部分值
     *
     * @param id
     * @return
     */
    public static String parseId(long id) {
        long sequenceNum = (id << (64 - SEQUENCE_BIT)) >> (64 - SEQUENCE_BIT);
        long machineNum = (id << (64 - MACHINE_BIT - SEQUENCE_BIT)) >> (64 - MACHINE_BIT);
        long dataCenterNum = (id << (64 - DATA_CENTER_BIT - MACHINE_BIT - SEQUENCE_BIT)) >> (64 - DATA_CENTER_BIT);
        long timeStampGap = id >> (64 - TIME_STAMP_BIT);

        long timeStamp = timeStampGap + START_STAMP;
        String time = new DateTime(timeStamp).toString("yyyy-MM-dd HH:mm:ss");

        return String.format("time:%s, dataCenter:%s, machineNum:%s, sequence:%s",
                time, dataCenterNum, machineNum, sequenceNum);
    }
}
