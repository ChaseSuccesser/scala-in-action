package com.ligx.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.MapMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @descrition
 * 1.利用Map集合的computeIfAbsent方法处理重复请求
 * 2.利用Guava Cache的缓存回收和回收监听器的功能来定期的删除Map集合中的key
 *   (因为Guava提供的Map不再支持key过期功能，所以借用Guava Cache来实现)
 * @author ligx on 17/12/07.
 */
public class RequestCollapse<R> {

    private static final Logger logger = LoggerFactory.getLogger(RequestCollapse.class);

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
    private int timeout; // timeout of future(ms)


    private Map<String, Future<R>> map = new MapMaker().concurrencyLevel(64).makeMap();


    private CacheLoader<String, Future<R>> cacheLoader = new CacheLoader<String, Future<R>>() {
        @Override
        public Future<R> load(String key) throws Exception {
            return pool.submit(callable);
        }
    };
    private RemovalListener<String, Future<R>> removalListener = notification -> {
        String key = notification.getKey();
        map.remove(key);
    };
    private LoadingCache<String, Future<R>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(windowTime, TimeUnit.MILLISECONDS)
            .maximumSize(5000)    /** 推荐大小: 大于 {@value windowTime} / 1000 * 请求的QPS*/
            .removalListener(removalListener)
            .build(cacheLoader);


    /**
     * 在windowTime时间窗口内，针对多个相同的重复请求，
     * 只有第一个请求会访问接口拿到结果，并缓存它的结果；其余请求直接使用第一个请求缓存的结果。
     *
     * @return
     */
    public R response() {
        Future<R> future = map.computeIfAbsent(repeatKey, s -> {
            try {
                return cache.get(s);
            } catch (ExecutionException e) {
                logger.error("RequestCollapse#response. key={}", repeatKey, e);
                return null;
            }
        });
        if (future == null) future = map.get(repeatKey);

        // TODO test 在调用future的get之前，map中对应的key被删除了，这种情况会发生么
        // TODO test future的超时时间和窗口时间协调
        try {
            if (future == null) {
                logger.warn("RequestCollapse#response. future is null! key={}", repeatKey);
                return null;
            }
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("RequestCollapse#response. key={}", repeatKey, e);
            return null;
        }
    }


    public RequestCollapse(Builder<R> builder) {
        this.repeatKey = builder.repeatKey;
        this.callable = builder.callable;
        this.timeout = builder.timeout;
        if (builder.pool != null) this.pool = builder.pool;
        if (builder.windowTime > 0) this.windowTime = builder.windowTime;
    }


    static class Builder<R> {
        // optional params
        private int windowTime;
        private ThreadPoolExecutor pool;

        // require params
        private String repeatKey;
        private Callable<R> callable;
        private int timeout;

        public Builder(String repeatKey, Callable<R> callable, int timeout) {
            this.repeatKey = repeatKey;
            this.callable = callable;
            this.timeout = timeout;
        }

        public Builder windowTimw(int windowTime) {
            this.windowTime = windowTime;
            return this;
        }

        public Builder pool(ThreadPoolExecutor pool) {
            this.pool = pool;
            return this;
        }


        public RequestCollapse<R> build() {
            if (this.callable == null || timeout <= 0 || (repeatKey == null || repeatKey.trim().equals(""))) {
                throw new IllegalArgumentException("lack require param:[callable] or [timeout] or [repeatKey]");
            }
            return new RequestCollapse<R>(this);
        }
    }
}
