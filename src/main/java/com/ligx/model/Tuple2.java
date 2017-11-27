package com.ligx.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Author: ligongxing.
 * Date: 2017年05月26日.
 */
@Getter
@Setter
@ToString
public class Tuple2<T1, T2> {

    private T1 t1;

    private T2 t2;

    public Tuple2(){}

    public Tuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}



