package com.ylan.test.circularReferences;

import com.ylan.spring.anno.Autowired;
import com.ylan.spring.anno.Component;

/**
 * @author by pepsi-wyl
 * @date 2023-04-22 10:48
 */

//@Component("c")
public class C implements CDInterface{
    // 采用 JDK 动态代理，注入的类型需要是接口类型
    @Autowired
    private CDInterface d;
}
