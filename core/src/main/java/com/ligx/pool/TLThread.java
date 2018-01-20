package com.ligx.pool;

/**
 * Author: ligongxing.
 * Date: 2018年01月12日.
 */
public class TLThread extends Thread {

    public TLThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.printf("%s 从 ThreadLocal 取数据：%d\n",
                Thread.currentThread().getName(), ThreadLocalTest.get());

        System.out.printf("%s 从 InheritableThreadLocal 取数据：%d\n",
                Thread.currentThread().getName(), InheritableThreadLocalTest.get());
    }


    public static void main(String[] args) {
        System.out.printf("%s 从 ThreadLocal 取数据：%d\n",
                Thread.currentThread().getName(), ThreadLocalTest.get());

        System.out.printf("%s 从 InheritableThreadLocal 取数据：%d\n",
                Thread.currentThread().getName(), InheritableThreadLocalTest.get());

        TLThread t1 = new TLThread("Child");
        t1.start();
    }
}
