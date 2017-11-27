package com.ligx.es;

import com.ligx.model.Tuple2;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Author: ligongxing.
 * Date: 2017年11月24日.
 */
public class EsBaseSearch {

    public static final String AGG_DATE_HISTO = "agg_date_histom";
    public static final String COUNT_SUM = "count_sum";


    public static BoolQueryBuilder buildBoolQuery(EsQueryParam queryParam) {
        // query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // range query
        long start = queryParam.getStart();
        long end = queryParam.getEnd();
        if (start > 0L && end > 0L) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp")
                    .gte(start)
                    .lte(end);
            boolQueryBuilder.must(rangeQueryBuilder);
        }

        // term query
        Map<String, Object> termQueryMap = queryParam.getTermQueryMap();
        if (MapUtils.isNotEmpty(termQueryMap)) {
            for (Map.Entry<String, Object> entry : termQueryMap.entrySet()) {
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
            }
        }

        // terms query
        Map<String, List<Object>> termsQueryMap = queryParam.getTermsQueryMap();
        if (MapUtils.isNotEmpty(termsQueryMap)) {
            for (Map.Entry<String, List<Object>> entry : termsQueryMap.entrySet()) {
                boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
            }
        }

        // regexp query
        Map<String, String> regexpQueryMap = queryParam.getRegexpQueryMap();
        if (MapUtils.isNotEmpty(regexpQueryMap)) {
            for (Map.Entry<String, String> entry : regexpQueryMap.entrySet()) {
                boolQueryBuilder.must(QueryBuilders.regexpQuery(entry.getKey(), entry.getValue()));
            }
        }

        // query_string query
        String queryExpr = queryParam.getQueryExpr();
        if (StringUtils.isNotBlank(queryExpr)) {
            boolQueryBuilder.must(QueryBuilders.queryStringQuery(queryExpr).analyzeWildcard(true));
        }

        // prefix query
        Map<String, Object> prefixQueryMap = queryParam.getPrefixQueryMap();
        if (MapUtils.isNotEmpty(prefixQueryMap)) {
            for (Map.Entry<String, Object> entry : prefixQueryMap.entrySet()) {
                boolQueryBuilder.must(QueryBuilders.prefixQuery(entry.getKey(), (String) entry.getValue()));
            }
        }

        return boolQueryBuilder;
    }


    /**
     * 聚合类型有:
     * 1.
     * termsAggFieldTuple
     * ----termsAggField2Tuple
     * --------interval
     * ------------sum (可选)
     *
     * 2.
     * termsAggFieldTuple
     * ----termsAggField2Tuple
     *
     * 3.
     * termsAggFieldTuple
     *
     * 4.
     * termsAggField2Tuple
     * ----interval
     * --------sum (可选)
     *
     * 5.
     * interval
     * ----sum (可选)
     *
     * @param queryParam
     * @return
     */
    public static TermsAggregationBuilder buildTermsAgg(EsQueryParam queryParam) {
        // aggregation, 具体构造语句参考test/resources/es_agent_booking-stat.json
        // 根据interval构建聚合
        DateHistogramAggregationBuilder dateHistogramAgg = null;
        DateHistogramInterval interval = queryParam.getInterval();
        if (interval != null) {
            dateHistogramAgg = AggregationBuilders.dateHistogram(AGG_DATE_HISTO)
                    .field("timestamp")
                    .dateHistogramInterval(interval)
                    .minDocCount(0)
                    .timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")));
            if (queryParam.isBuildSumAgg()) {
                dateHistogramAgg.subAggregation(AggregationBuilders.sum(COUNT_SUM).field("count"));
            }
        }

        // 根据termsAggField2构建聚合.若上面的聚合不为空，则把它作为这个聚合的子聚合
        TermsAggregationBuilder termsAgg2 = null;
        if (queryParam.getTermsAggField2Tuple() != null) {
            String termsAggField2 = queryParam.getTermsAggField2Tuple().getT1();
            termsAgg2 = AggregationBuilders.terms(termsAggField2.toUpperCase())
                    .field(termsAggField2)
                    .minDocCount(0)
                    .size(queryParam.getTermsAggField2Tuple().getT2());

            if (interval != null) {
                termsAgg2.subAggregation(dateHistogramAgg);
            }
        }

        // 根据termsAggField构建聚合.如果termsAggFieldTuple不为空，则把上面的聚合作为子聚合
        TermsAggregationBuilder termsAgg1;
        Tuple2<String, Integer> termsAggFieldTuple = queryParam.getTermsAggFieldTuple();
        if (termsAggFieldTuple != null) {
            termsAgg1 = AggregationBuilders.terms(termsAggFieldTuple.getT1().toUpperCase())
                    .field(termsAggFieldTuple.getT1())
                    .minDocCount(0)
                    .size(termsAggFieldTuple.getT2() == 0 ? 10 : termsAggFieldTuple.getT2());

            if (termsAgg2 != null) {
                termsAgg1.subAggregation(termsAgg2);
            }
        } else {
            termsAgg1 = termsAgg2;
        }

        return termsAgg1;
    }
}
