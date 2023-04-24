package com.ylan.spring.aop.advisor;

import com.ylan.spring.core.Ordered;


// 通知接口 Spring 中此接口并没有实现 Ordered，而是使用别的方法进行排序
public interface Advice extends Ordered {

}
