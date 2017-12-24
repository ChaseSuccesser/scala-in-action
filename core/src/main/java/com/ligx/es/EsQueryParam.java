package com.ligx.es;

import com.ligx.model.Tuple2;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.List;
import java.util.Map;

/**
 * Author: ligongxing.
 * Date: 2017年06月08日.
 */
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


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHitSize() {
        return hitSize;
    }

    public void setHitSize(int hitSize) {
        this.hitSize = hitSize;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Map<String, Object> getTermQueryMap() {
        return termQueryMap;
    }

    public void setTermQueryMap(Map<String, Object> termQueryMap) {
        this.termQueryMap = termQueryMap;
    }

    public Map<String, List<Object>> getTermsQueryMap() {
        return termsQueryMap;
    }

    public void setTermsQueryMap(Map<String, List<Object>> termsQueryMap) {
        this.termsQueryMap = termsQueryMap;
    }

    public Map<String, String> getRegexpQueryMap() {
        return regexpQueryMap;
    }

    public void setRegexpQueryMap(Map<String, String> regexpQueryMap) {
        this.regexpQueryMap = regexpQueryMap;
    }

    public String getQueryExpr() {
        return queryExpr;
    }

    public void setQueryExpr(String queryExpr) {
        this.queryExpr = queryExpr;
    }

    public Map<String, Object> getPrefixQueryMap() {
        return prefixQueryMap;
    }

    public void setPrefixQueryMap(Map<String, Object> prefixQueryMap) {
        this.prefixQueryMap = prefixQueryMap;
    }

    public Tuple2<String, Integer> getTermsAggFieldTuple() {
        return termsAggFieldTuple;
    }

    public void setTermsAggFieldTuple(Tuple2<String, Integer> termsAggFieldTuple) {
        this.termsAggFieldTuple = termsAggFieldTuple;
    }

    public Tuple2<String, Integer> getTermsAggField2Tuple() {
        return termsAggField2Tuple;
    }

    public void setTermsAggField2Tuple(Tuple2<String, Integer> termsAggField2Tuple) {
        this.termsAggField2Tuple = termsAggField2Tuple;
    }

    public DateHistogramInterval getInterval() {
        return interval;
    }

    public void setInterval(DateHistogramInterval interval) {
        this.interval = interval;
    }

    public boolean isBuildSumAgg() {
        return buildSumAgg;
    }

    public void setBuildSumAgg(boolean buildSumAgg) {
        this.buildSumAgg = buildSumAgg;
    }

    public Tuple2<String, Integer> getTermsAggScriptTuple() {
        return termsAggScriptTuple;
    }

    public void setTermsAggScriptTuple(Tuple2<String, Integer> termsAggScriptTuple) {
        this.termsAggScriptTuple = termsAggScriptTuple;
    }
}
