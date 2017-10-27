package com.ligx.pool;


import java.util.concurrent.RecursiveTask;

/**
 * Author: ligongxing.
 * Date: 2017年09月23日.
 */
//RecursiveTask为ForkJoinTask的抽象子类，有返回值的任务
class SumTask extends RecursiveTask<Integer> {

    // 每个"小任务"最多只打印50个数
    private static final int MAX = 20;
    private int arr[];
    private int start;
    private int end;

    SumTask(int arr[], int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        // 当end-start的值小于MAX时候，开始打印
        if ((end - start) < MAX) {
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        } else {
            System.err.println("=====任务分解======");
            // 将大任务分解成两个小任务
            int middle = (start + end) / 2;
            SumTask left = new SumTask(arr, start, middle);
            SumTask right = new SumTask(arr, middle, end);

            // 并行执行两个小任务
            left.fork();
            right.fork()
            ;
            // 把两个小任务累加的结果合并起来
            return left.join() + right.join();
        }
    }

}
