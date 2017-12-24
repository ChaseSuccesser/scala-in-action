package com.ligx.es;

import com.ligx.model.Tuple2;
import com.ligx.model.Tuple3;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Author: ligongxing.
 * Date: 2017年05月28日.
 */
public class EsSearch extends EsBaseSearch {

    private static final Logger logger = LoggerFactory.getLogger(EsSearch.class);

    /**
     * aggregation name
     */
    private static final String AGG_ITEM = "agg_item";


    // ---------------------------------------------- terms aggregation
    /**
     * 根据搜索结果构建Terms聚合 (默认按文档数量降序排序)
     *
     * @param queryParam
     * @return
     */
    public static List<Tuple2<String, Integer>> termsAggregation(EsQueryParam queryParam) {
        return termsAggregation(queryParam, true);
    }

    /**
     * 根据搜索结果构建Terms聚合
     *
     * @param queryParam
     * @param isQueryHotData 是否查询热门数据
     * @return
     */
    public static List<Tuple2<String, Integer>> termsAggregation(EsQueryParam queryParam, boolean isQueryHotData) {
        // query
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(queryParam);

        // terms aggregation
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(AGG_ITEM);
        if (queryParam.getTermsAggFieldTuple() != null) {
            termsAggregationBuilder.field(queryParam.getTermsAggFieldTuple().getT1());
            termsAggregationBuilder.size(queryParam.getTermsAggFieldTuple().getT2());
        } else if (queryParam.getTermsAggScriptTuple() != null) {
            Script script = new Script(queryParam.getTermsAggScriptTuple().getT1());
            termsAggregationBuilder.script(script);
            termsAggregationBuilder.size(queryParam.getTermsAggScriptTuple().getT2());
        } else {
            return new ArrayList<>();
        }

        if (isQueryHotData) {
            termsAggregationBuilder.order(Terms.Order.count(false));
        } else {
            termsAggregationBuilder.order(Terms.Order.count(true));
        }

        SearchResponse searchResponse = null;
        try {
            TransportClient client = EsClient.getClient();
            searchResponse = client.prepareSearch(queryParam.getIndex())
                    .setTypes(queryParam.getType())
                    .setSize(0)
                    .setQuery(boolQueryBuilder)
                    .addAggregation(termsAggregationBuilder)
                    .get();
        } catch (Exception e) {
            logger.error("EsSearch#termsAggregation, EsQueryParam={}", queryParam, e);
        }
        return getTermsAggResp(searchResponse);
    }


    /**
     * 获取Terms聚合的响应结果
     *
     * @param searchResponse
     * @return
     */
    private static List<Tuple2<String, Integer>> getTermsAggResp(SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new ArrayList<>();
        }

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            logger.warn("EsSearch#getTermsAggResp, Aggregation is empty. SearchResponse={}", searchResponse);
            return new ArrayList<>();
        }

        Terms terms = aggregations.get(AGG_ITEM);
        List<Terms.Bucket> bucketList = terms.getBuckets();
        List<Tuple2<String, Integer>> keyValueList = new ArrayList<>(bucketList.size());
        for (Terms.Bucket bucket : bucketList) {
            String key = String.valueOf(bucket.getKey());
            Integer count = Integer.valueOf(String.valueOf(bucket.getDocCount()));
            keyValueList.add(new Tuple2<>(key, count));
        }
        return keyValueList;
    }




    // ---------------------------------------------- date_histogram aggregation
    /**
     * 从搜索结果中构建Date_Histogram聚合
     *
     * @param queryParam
     * @return
     */
    public static List<Tuple2<String, Integer>> dateHistogramAggregation(EsQueryParam queryParam) {
        // query
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(queryParam);

        // aggregation
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram(AGG_ITEM)
                .field("timestamp")
                .dateHistogramInterval(queryParam.getInterval())
                .timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")))
                .minDocCount(0);

        SearchResponse searchResponse = null;
        try {
            TransportClient client = EsClient.getClient();
            searchResponse = client.prepareSearch(queryParam.getIndex())
                    .setTypes(queryParam.getType())
                    .setSize(0)
                    .setQuery(boolQueryBuilder)
                    .addAggregation(dateHistogramAggregationBuilder)
                    .get();
        } catch (Exception e) {
            logger.error("EsSearch#dateHistogramAggregation, EsQueryParam={}", queryParam, e);
        }
        return getDateHistogramAggResp(searchResponse);
    }

    /**
     * 获取Date_Histogram聚合的响应结果
     *
     * @param searchResponse
     * @return
     */
    private static List<Tuple2<String, Integer>> getDateHistogramAggResp(SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new ArrayList<>();
        }

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            logger.warn("EsSearch#getDateHistogramAggResp, Aggregation is empty. SearchResponse={}", searchResponse);
            return new ArrayList<>();
        }

        Histogram histogram = aggregations.get(AGG_ITEM);
        List<Histogram.Bucket> bucketList = histogram.getBuckets();
        List<Tuple2<String, Integer>> keyValueList = new ArrayList<>(bucketList.size());
        for (Histogram.Bucket bucket : bucketList) {
            DateTime dateTime = (DateTime) bucket.getKey();
            Integer count = Integer.valueOf(String.valueOf(bucket.getDocCount()));
            keyValueList.add(new Tuple2<>(String.valueOf(dateTime.getMillis()), count));
        }
        return keyValueList;
    }


    // ---------------------------------------------- terms and date_histogram aggregation
    /**
     * 构建terms和date_histogram聚合
     *
     * @param queryParam
     * @return
     */
    public static Map<String, List<Tuple2<Long, Long>>> termsAndDateHistogramAgg(EsQueryParam queryParam) {
        // query
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(queryParam);
        // aggregation
        TermsAggregationBuilder termsAggregationBuilder = buildTermsAgg(queryParam);

        SearchResponse searchResponse = null;
        try {
            TransportClient client = EsClient.getClient();
            searchResponse = client.prepareSearch(queryParam.getIndex())
                    .setTypes(queryParam.getType())
                    .setSize(0)
                    .setQuery(boolQueryBuilder)
                    .addAggregation(termsAggregationBuilder)
                    .addSort("timestamp", SortOrder.ASC)
                    .get();
        } catch (Exception e) {
            logger.error("EsSearch#termsAndDateHistogramAgg.", e);
        }

        return getTermsAndDateHistogramAggResp(queryParam, searchResponse);
    }

    private static Map<String, List<Tuple2<Long, Long>>> getTermsAndDateHistogramAggResp(
            EsQueryParam queryParam, SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new HashMap<>();
        }

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            return new HashMap<>();
        }

        Terms terms = aggregations.get(queryParam.getTermsAggField2Tuple().getT1().toUpperCase());
        List<Terms.Bucket> termsBucketList = terms.getBuckets();
        Map<String, List<Tuple2<Long, Long>>> result = new HashMap<>(termsBucketList.size());
        for (Terms.Bucket termsBucket : termsBucketList) {
            String key = String.valueOf(termsBucket.getKey());

            Histogram histogram = termsBucket.getAggregations().get(EsBaseSearch.AGG_DATE_HISTO);
            List<Histogram.Bucket> histoBucketList = histogram.getBuckets();
            List<Tuple2<Long, Long>> tuple2List = new ArrayList<>(histoBucketList.size());
            for (Histogram.Bucket histoBucket : histoBucketList) {
                DateTime dateTime = (DateTime) histoBucket.getKey();
                long time = dateTime.getMillis();
                long count = histoBucket.getDocCount();

                tuple2List.add(new Tuple2<>(time, count));
            }

            result.put(key, tuple2List);
        }

        return result;
    }


    // ---------------------------------------------- 多层嵌套aggregation
    /**
     * 类似于SQL:
     * <p>
     * select sum(count)
     * from table
     * where time <= end and time >= start
     * and agentId = '9600'
     * and (eventName = 'BOOKING_VALIDATE_SUCC' or termsAggsField2 = 'BOOKING_PRICE_CHANGE')
     * and eventName = 'BOOKING.*'
     * group by agentId,eventName,time(1d) fill(0)
     * order by time;
     */
    public static Object multiAggregation(EsQueryParam queryParam) {
        // query
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(queryParam);
        // aggregation
        TermsAggregationBuilder termsAggregationBuilder = buildTermsAgg(queryParam);

        SearchResponse searchResponse = null;
        try {
            TransportClient client = EsClient.getClient();
            searchResponse = client.prepareSearch(queryParam.getIndex())
                    .setTypes(queryParam.getType())
                    .setQuery(boolQueryBuilder)
                    .setSize(0)
                    .addAggregation(termsAggregationBuilder)
                    .addSort("timestamp", SortOrder.ASC)
                    .get();
        } catch (Exception e) {
            logger.error("EsSearch#multiAggregation.", e);
        }

        // 针对不同的聚合条件，返回不同的聚合响应结果
        if (queryParam.getTermsAggFieldTuple() == null && queryParam.getTermsAggField2Tuple() != null
                && queryParam.getInterval() != null) {
            // 根据后面两个聚合条件进行分组
            return getMultiAggResp(queryParam, searchResponse);
        } else if (queryParam.getTermsAggFieldTuple() != null && queryParam.getTermsAggField2Tuple() != null
                && queryParam.getInterval() == null) {
            // 根据前面两个聚合条件进行分组
            return getMultiAggResp2(queryParam, searchResponse);
        } else if (queryParam.getTermsAggFieldTuple() != null && queryParam.getTermsAggField2Tuple() != null
                && queryParam.getInterval() != null) {
            // 根据全部的三个聚合条件进行分组
            return getMultiAggResp3(queryParam, searchResponse);
        } else {
            return null;
        }
    }

    private static List<Tuple3<String, Long, Long>> getMultiAggResp(EsQueryParam queryParam, SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new ArrayList<>();
        }

        long took = searchResponse.getTook().getMillis();
        boolean isTimedOut = searchResponse.isTimedOut();
        logger.info("EsSearch#getMultiAggResp, took={}, isTimedOut={}", took, isTimedOut);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            logger.warn("EsSearch#getMultiAggResp, Aggregations is null, SearchResponse={}", searchResponse);
            return new ArrayList<>();
        }

        Terms terms = aggregations.get(queryParam.getTermsAggField2Tuple().getT1().toUpperCase());
        return getTermsAggResp(queryParam, terms);
    }

    private static Map<String, List<Tuple2<String, Long>>> getMultiAggResp2(EsQueryParam queryParam, SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new HashMap<>();
        }

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            logger.warn("EsSearch#getMultiAggResp2, Aggregations is null, SearchResponse={}", searchResponse);
            return new HashMap<>();
        }

        Terms terms = aggregations.get(queryParam.getTermsAggFieldTuple().getT1().toUpperCase());
        List<Terms.Bucket> outerTermsBucketList = terms.getBuckets();
        Map<String, List<Tuple2<String, Long>>> result = new HashMap<>(outerTermsBucketList.size());
        for (Terms.Bucket outerTermsBucket : outerTermsBucketList) {
            String outerKey = String.valueOf(outerTermsBucket.getKey());

            Terms innerTerms = outerTermsBucket.getAggregations().get(queryParam.getTermsAggField2Tuple().getT1().toUpperCase());
            List<Terms.Bucket> innerTermsBucketList = innerTerms.getBuckets();
            List<Tuple2<String, Long>> tuple2List = new ArrayList<>(innerTermsBucketList.size());
            for (Terms.Bucket innerTermsBucket : innerTermsBucketList) {
                String innerKey = String.valueOf(innerTermsBucket.getKey());
                long count = innerTermsBucket.getDocCount();
                tuple2List.add(new Tuple2<>(innerKey, count));
            }
            result.put(outerKey, tuple2List);
        }

        return result;
    }

    private static Map<String, List<Tuple3<String, Long, Long>>> getMultiAggResp3(EsQueryParam queryParam, SearchResponse searchResponse) {
        if (searchResponse == null) {
            return new HashMap<>();
        }

        long took = searchResponse.getTook().getMillis();
        boolean isTimedOut = searchResponse.isTimedOut();
        logger.info("EsSearch#getMultiAggResp3, took={}, isTimedOut={}", took, isTimedOut);

        Aggregations aggregations = searchResponse.getAggregations();
        if (aggregations == null) {
            logger.warn("EsSearch#getMultiAggResp3, Aggregations is null, SearchResponse={}", searchResponse);
            return new HashMap<>();
        }

        Terms outerTerms = aggregations.get(queryParam.getTermsAggFieldTuple().getT1().toUpperCase());
        List<Terms.Bucket> outerTermsBucketList = outerTerms.getBuckets();
        Map<String, List<Tuple3<String, Long, Long>>> result = new HashMap<>();
        for (Terms.Bucket outerTermsBucket : outerTermsBucketList) {
            String key = String.valueOf(outerTermsBucket.getKey());

            Terms innerTerms = outerTermsBucket.getAggregations().get(queryParam.getTermsAggField2Tuple().getT1().toUpperCase());
            List<Tuple3<String, Long, Long>> tuple3List = getTermsAggResp(queryParam, innerTerms);

            result.put(key, tuple3List);
        }
        return result;
    }

    private static List<Tuple3<String, Long, Long>> getTermsAggResp(EsQueryParam queryParam, Terms terms) {
        List<Tuple3<String, Long, Long>> tuple3List = new ArrayList<>();

        List<Terms.Bucket> termsBucketList = terms.getBuckets();
        for (Terms.Bucket termsBucket : termsBucketList) {
            String key = (String) termsBucket.getKey();

            Histogram histogram = termsBucket.getAggregations().get(EsBaseSearch.AGG_DATE_HISTO);
            List<Histogram.Bucket> histogramBuckList = histogram.getBuckets();
            for (Histogram.Bucket histogramBucket : histogramBuckList) {
                DateTime dateTime = (DateTime) histogramBucket.getKey();
                long time = dateTime.getMillis();

                long sum;
                if (queryParam.isBuildSumAgg()) {
                    Sum sumAggs = histogramBucket.getAggregations().get(EsBaseSearch.COUNT_SUM);
                    sum = (long) sumAggs.getValue();
                } else {
                    sum = histogramBucket.getDocCount();
                }

                tuple3List.add(new Tuple3<>(key, time, sum));
            }
        }
        return tuple3List;
    }


    // ---------------------------------------------- search
    /**
     * 查询ES数据-----通用的方法,可以满足大部分查询场景
     *
     * @param queryParam
     * @return
     */
    public static SearchResponse query(EsQueryParam queryParam) {
        // query
        BoolQueryBuilder boolQueryBuilder = buildBoolQuery(queryParam);

        SearchResponse searchResponse = null;
        try {
            TransportClient client = EsClient.getClient();
            searchResponse = client.prepareSearch(queryParam.getIndex())
                    .setTypes(queryParam.getType())
                    .setQuery(boolQueryBuilder)
                    .addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC).missing("_last"))
                    .setSize(queryParam.getHitSize())
                    .setVersion(true)
                    .get();
        } catch (Exception e) {
            logger.error("EsSearch#query, EsQueryParam={}", queryParam, e);
        }

        return searchResponse;
    }
}
