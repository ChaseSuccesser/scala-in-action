package com.ligx.rxjava;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Author: ligongxing.
 * Date: 2017年07月14日.
 */
public class ObservableDemo {

    @Test
    public void test(){
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("received:" + aLong);

                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
