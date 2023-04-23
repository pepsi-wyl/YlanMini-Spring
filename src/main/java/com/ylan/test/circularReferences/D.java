package com.ylan.test.circularReferences;

import com.ylan.spring.anno.Autowired;
import com.ylan.spring.anno.Component;
import com.ylan.spring.anno.Lazy;

/**
 * @author by pepsi-wyl
 * @date 2023-04-22 10:48
 */

//@Component("d")
public class D implements CDInterface {

    // 采用 JDK 动态代理，注入的类型需要是接口类型

    private CDInterface c;

    // 采用 JDK 动态代理
    public D(@Lazy CDInterface c) {

    }
}
