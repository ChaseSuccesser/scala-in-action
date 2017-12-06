package com.ligx.utils;

import org.junit.Test;

public class RequestCollapseTest {

    @Test
    public void collapse() throws Exception {
        RequestCollapse<String> requestCollapse = new RequestCollapse.Builder<String>("BJS_HKG_2018-02-11", null, 2).build();

        String result = requestCollapse.response();
    }

}