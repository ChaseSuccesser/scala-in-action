package com.ligx.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Author: ligongxing.
 * Date: 2017年10月30日.
 */
public class CollectionUtil {

    /**
     * 比较两个List的集合元素是否完全相同
     *
     * @param list1
     * @param list2
     * @param <T>   集合元素类型为Java原生类型
     * @return
     */
    public static <T> boolean compareCollection(List<T> list1, List<T> list2) {
        if (list1 == null && list2 == null) {
            return true;
        } else if (list1 == null) {
            return false;
        } else if (list2 == null) {
            return false;
        }

        if (list1.size() != list2.size()) {
            return false;
        }
        // list1含有只存在list1而不存在list2的元素，删除它们(list1发生了改变)，返回true
        // list1没有只存在list1而不存在list2中元素，list1不改变，返回false
        return !list1.retainAll(list2);
    }


    /**
     * 比较两个List的集合元素是否完全相同
     *
     * @param list1
     * @param list2
     * @param compareFunc 使用这个函数比较两个集合中的元素是否相同
     * @param <T>         自定义的类型
     * @param <U>         自定义的类型
     * @return
     */
    public static <T, U> boolean compareCollection(List<T> list1, List<U> list2, BiFunction<T, U, Boolean> compareFunc) {
        if (list1 != null) {
            if (list2 == null) {
                return false;
            } else if (list1.size() != list2.size()) {
                return false;
            } else {
                int size = list1.size();
                int equalCount = 0;
                for (T t : list1) {
                    for (U u : list2) {
                        if (compareFunc.apply(t, u)) {
                            equalCount += 1;
                            break;
                        }
                    }
                }
                return equalCount == size;
            }
        } else {
            return list2 == null;
        }
    }


    /**
     * 过滤掉两个集合的交集，保留各自的数据
     *
     * @param list1
     * @param list2
     * @param compareFunc
     * @param <T>
     * @param <U>
     */
    public static <T, U> void filterIntersection(List<T> list1, List<U> list2, BiFunction<T, U, Boolean> compareFunc) {
        if (CollectionUtils.isNotEmpty(list1) && CollectionUtils.isNotEmpty(list2)) {
            for (Iterator<T> tIt = list1.iterator(); tIt.hasNext();) {
                T t = tIt.next();
                for (Iterator<U> uIt = list2.iterator(); uIt.hasNext();) {
                    U u = uIt.next();
                    if (compareFunc.apply(t, u)) {
                        tIt.remove();
                        uIt.remove();
                        break;
                    }
                }
            }
        }
    }


    /**
     * 两个集合中相同的元素保留第二个集合的，不同的元素相互补充
     *
     * @param list1
     * @param list2
     * @param compareFunc
     * @param <T>
     * @return
     */
    public static <T> List<T> aggregateCollection(List<T> list1, List<T> list2, BiFunction<T, T, Boolean> compareFunc) {
        if (CollectionUtils.isEmpty(list1) && CollectionUtils.isEmpty(list2)) {
            return new ArrayList<>();
        } else if (CollectionUtils.isNotEmpty(list1) && CollectionUtils.isEmpty(list2)) {
            return list1;
        } else if(CollectionUtils.isEmpty(list2) && CollectionUtils.isNotEmpty(list2)) {
            return list2;
        }

        List<T> equalElemList = new ArrayList<>();

        for (Iterator<T> it1 = list1.iterator(); it1.hasNext();) {
            T t1 = it1.next();
            for (Iterator<T> it2 = list2.iterator(); it2.hasNext();) {
                T t2 = it2.next();
                if (compareFunc.apply(t1, t2)) {
                    equalElemList.add(t2);
                    it1.remove();
                    it2.remove();
                    break;
                }
            }
        }

        list1.addAll(equalElemList);
        list1.addAll(list2);

        return list1;
    }
}
