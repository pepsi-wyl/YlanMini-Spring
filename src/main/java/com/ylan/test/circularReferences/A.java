package com.ylan.test.circularReferences;

import com.ylan.spring.anno.Autowired;
import com.ylan.spring.anno.Component;
import com.ylan.spring.anno.Scope;
import com.ylan.spring.interfaces.DisposableBean;

/**
 * @author by pepsi-wyl
 * @date 2023-04-22 10:02
 */

// A B 循环引用 需要至少有一个是单例Bean

//@Component("a")
//@Scope("prototype")
public class A implements DisposableBean {
    @Autowired
    private B b;

    @Override
    public void destroy() throws Exception {
        System.out.println("A destroy...");
    }
}
