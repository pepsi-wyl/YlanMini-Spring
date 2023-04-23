package com.ylan.spring.core;

/**
 * 排序
 * @author by pepsi-wyl
 * @date 2023-04-20 20:35
 */

public interface Ordered {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    int getOrder();
}
