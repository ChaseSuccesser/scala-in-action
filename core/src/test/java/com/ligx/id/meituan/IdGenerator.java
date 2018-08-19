package com.ligx.id.meituan;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);

    public static final int TOTAL_BITS_LENGTH = 63;
    public static final int TIME_BITS_LENGTH = 41;
    public static final int NODE_BITS_LENGTH = 10;
    private static final int COUNT_BITS_LENGTH = 12;
    private static final long TIME_BITS_MASK = 2199023255551L;
    private static final int TIME_BITS_SHIFT_SIZE = 22;
    private static final int NODE_BITS_MASK = 1023;
    private static final int MAX_COUNTER = 4096;
    private int nodeId;
    private AtomicInteger counter;
    private long lastMillisecond;
    private static IdGenerator instance = new IdGenerator();

    private IdGenerator() {
        this.nodeId = (int) (Math.random() * 1000);
        this.counter = new AtomicInteger(0);
    }

    public static long get() {
        long id = 0L;
        try {
            id = IdGenerator.instance.nextTicket();
        } catch (Exception e) {
            LOGGER.error("IdGenerator->get:error", e);
        }
        if (id == 0L) {
            try {
                Thread.sleep(3L);
                id = IdGenerator.instance.nextTicket();
            } catch (Exception e) {
                LOGGER.error("IdGenerator->get:error", e);
            }
        }
        if (id == 0L) {
            return System.currentTimeMillis() + (int) (Math.random() * 10000);
        }
        return id;
    }

    private synchronized long nextTicket() {
        final long currentMillisecond = System.currentTimeMillis();
        if (currentMillisecond < this.lastMillisecond) {
            throw new RuntimeException("time is out of sync by " + (this.lastMillisecond - currentMillisecond) + "ms");
        }
        long ts = currentMillisecond & 0x1FFFFFFFFFFL;
        ts <<= 22;
        final int count = this.counter.incrementAndGet();
        if (currentMillisecond == this.lastMillisecond && count >= 4096) {
            throw new RuntimeException("too much requests cause counter overflow");
        }
        if (count >= 4096) {
            this.counter = new AtomicInteger(0);
        }
        final int node = (this.nodeId & 0x3FF) << 12;
        this.lastMillisecond = currentMillisecond;
        return ts + node + this.counter.get();
    }

    public static long timeStartId(final long timeMs) {
        long ts = timeMs & 0x1FFFFFFFFFFL;
        ts <<= 22;
        return ts;
    }

    public static void main(final String[] args) {
        System.out.println(get());
        final Calendar c = Calendar.getInstance();
        System.out.println(timeStartId(c.getTimeInMillis()));
        c.set(14, 0);
        c.set(13, 0);
        c.set(12, 0);
        c.set(11, 0);
        c.add(5, 1);
        System.out.println(timeStartId(c.getTimeInMillis()));
    }

}
