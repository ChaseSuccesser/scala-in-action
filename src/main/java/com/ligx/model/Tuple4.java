package com.ligx.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Author: ligongxing.
 * Date: 2017年05月27日.
 */
@Getter
@Setter
@ToString
public class Tuple4<T1, T2, T3, T4> {

    private T1 t1;

    private T2 t2;

    private T3 t3;

    private T4 t4;

    public Tuple4() {
    }

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }
}
