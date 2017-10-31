package com.ligx.utils;

import java.util.List;

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
     * @param <T>
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
}
