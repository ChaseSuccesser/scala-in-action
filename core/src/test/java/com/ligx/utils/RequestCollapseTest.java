package com.ligx.utils;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestCollapseTest {

    private static RequestCollapse<String> requestCollapse = new RequestCollapse<>();

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private ExecutorService pool = Executors.newCachedThreadPool();


    /**
     * 模拟多个重复的请求调用b服务的接口
     *
     * @throws Exception
     */
    @Test
    public void responseWithNormal() throws Exception {
        Runnable r1 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r2 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r3 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r4 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r5 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        pool.execute(r1);
        pool.execute(r2);
        pool.execute(r3);
        pool.execute(r4);
        pool.execute(r5);

        Thread.sleep(6000); // 暂停一段时间再发送请求，暂停的时间大于RequestCollapse的窗口时间
        System.out.println("sleep end");

        Runnable r6 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r7 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r8 = () -> {
            String result = serviceWithNormal("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        pool.execute(r6);
        pool.execute(r7);
        pool.execute(r8);


        Thread.sleep(Long.MAX_VALUE);
    }

    private String serviceWithNormal(String request) {
        return downStreamService(request);
    }


    /**
     * 模拟多个重复的请求调用b服务的接口
     * 测试一个时间窗口内的重复请求是否只调用下游接口一次
     *
     * @throws Exception
     */
    @Test
    public void responseWithCollapse() throws Exception {
        Runnable r0 = () -> {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            while (true) {
                System.out.println(atomicInteger.incrementAndGet());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable r1 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r2 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r3 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r4 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r5 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r9 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r10 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r11 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r12 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        pool.execute(r0);
        pool.execute(r1);
        pool.execute(r2);
        pool.execute(r3);
        pool.execute(r4);
        pool.execute(r5);
        pool.execute(r9);
        pool.execute(r10);
        pool.execute(r11);
        pool.execute(r12);

        Thread.sleep(6000); // 暂停一段时间再发送请求，暂停的时间大于RequestCollapse的窗口时间
        System.out.println("sleep end");

        Runnable r6 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r7 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        Runnable r8 = () -> {
            String result = serviceWithCollapse("BJS_HKG_2017-12-12");
            System.out.println("response: " + result);
        };
        pool.execute(r6);
        pool.execute(r7);
        pool.execute(r8);


        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 模拟b服务提供了一个接口，它会调用c服务的接口
     *
     * @param request
     * @return
     */
    private String serviceWithCollapse(String request) {
        requestCollapse.withRepeatKey(request).withCallable(() -> downStreamService(request)).withFutureTimeout(7500);

        return requestCollapse.response();
    }


    /**
     * 模拟c服务提供了一个接口
     *
     * @param request
     * @return
     */
    private String downStreamService(String request) {
        try {
            Thread.sleep(7000);
        } catch (Exception e) {
            return "";
        }
        return request + "---" + atomicInteger.incrementAndGet();
    }
}