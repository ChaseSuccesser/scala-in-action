package com.ligx.utils;

import com.google.common.collect.MapMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ligx on 17/12/07.
 * @descrition 在windowTime时间窗口内，针对多个相同的重复请求，
 * 只有第一个请求会访问接口拿到结果，并缓存它的结果；其余请求直接使用第一个请求缓存的结果。
 * <p>
 * 1.利用Map集合的computeIfAbsent方法处理重复请求
 * 2.利用一个定时线程来定期的删除Map集合中的key
 */
public class RequestCollapse<R> {

    private static final Logger logger = LoggerFactory.getLogger(RequestCollapse.class);

    private AtomicBoolean startUp = new AtomicBoolean(false);

    // optional params
    private int windowTime = 5000;  // 时间窗口(ms)
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024),
            r -> {
                AtomicLong atomicLong = new AtomicLong(0);
                return new Thread(r, String.format("RequestCollapseThreadPool-%s", String.valueOf(atomicLong.incrementAndGet())));
            }, (r, executor) -> {
    });
    // required params
    private String repeatKey;
    private Callable<R> callable; // 封装实际业务逻辑
    private int futureTimeout; // futureTimeout of future(ms)


    private Map<String, Future<R>> map = new MapMaker().concurrencyLevel(64).makeMap();


    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private void startScheduler() {
        if (startUp.compareAndSet(false, true)) {
            Runnable task = () -> {
                map.clear();
            };
            scheduler.scheduleAtFixedRate(task, windowTime, windowTime, TimeUnit.MILLISECONDS);
        }
    }


    public R response() {
        startScheduler();

        Future<R> future = map.computeIfAbsent(repeatKey, s -> pool.submit(callable));
        if (future == null) {
            future = map.get(repeatKey);
        }

        try {
            /*
             有可能会出现：非重复请求的key添加到map -> 定时线程清空map -> get非重复请求的key对应的value，value为null。这种在临界时出现的并发问题。
             会导致这个请求丢失，无法响应。
             所以，需要重新提交一次请求
            */
            if (future == null) {
                logger.warn("RequestCollapse#response. future is null! key={}", repeatKey);
                future = pool.submit(callable);
            }
            return future.get(futureTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("RequestCollapse#response. key={}", repeatKey, e);
            return null;
        }
    }


    private RequestCollapse() {
    }

    private static RequestCollapse instance = new RequestCollapse();

    public static <R> RequestCollapse<R> getInstance() {
        return instance;
    }

    public RequestCollapse withWindowTime(int windowTime) {
        this.windowTime = windowTime;
        return this;
    }

    public RequestCollapse withPool(ThreadPoolExecutor pool) {
        this.pool = pool;
        return this;
    }

    public RequestCollapse withRepeatKey(String repeatKey) {
        this.repeatKey = repeatKey;
        return this;
    }

    public RequestCollapse withCallable(Callable<R> callable) {
        this.callable = callable;
        return this;
    }

    public RequestCollapse withFutureTimeout(int futureTimeout) {
        this.futureTimeout = futureTimeout;
        return this;
    }
}
