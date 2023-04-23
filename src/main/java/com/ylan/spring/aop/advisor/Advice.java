package com.ylan.spring.aop.advisor;

import com.ylan.spring.core.Ordered;

/**
 * 通知 Spring 中此接口并没有实现 Ordered，而是使用别的方法进行排序
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 21:14
 */

public interface Advice extends Ordered {

}
