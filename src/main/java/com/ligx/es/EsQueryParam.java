package com.ligx.es;

import com.ligx.model.Tuple2;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.List;
import java.util.Map;

/**
 * Author: ligongxing.
 * Date: 2017年06月08日.
 */
@Getter
@Setter
@Builder
public class EsQueryParam {

    // elasticsearch index
    private String index;

    // elasticsearch type
    private String type;

    // 查询结果数量
    private int hitSize;


    // -----------------------------construct query condition
    // range query condition
    private long start;
    private long end;

    // term query condition
    private Map<String, Object> termQueryMap;

    // terms query condition
    private Map<String, List<Object>> termsQueryMap;

    // regexp query condition
    private Map<String, String> regexpQueryMap;

    // query_string query condition
    private String queryExpr;

    // prefix query condition
    private Map<String, Object> prefixQueryMap;


    // -----------------------------construct aggregation condition
    // terms aggregation field (第一个参数是聚合的字段名；第二个参数设定聚合返回的bucket数量, 默认是10)
    private Tuple2<String, Integer> termsAggFieldTuple;

    // terms aggregation field (第一个参数是聚合的字段名；第二个参数设定聚合返回的bucket数量, 默认是10)
    private Tuple2<String, Integer> termsAggField2Tuple;

    // date_histogram aggregation time interval
    private DateHistogramInterval interval;

    // 是否构造sum度量聚合
    private boolean buildSumAgg;

    // 使用Script构建Terms聚合
    private Tuple2<String, Integer> termsAggScriptTuple;
}
