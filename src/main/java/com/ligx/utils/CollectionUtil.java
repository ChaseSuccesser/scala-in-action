package com.ligx.utils;

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
}
