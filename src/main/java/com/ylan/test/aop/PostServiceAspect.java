package com.ylan.test.aop;

import com.ylan.spring.anno.Component;
import com.ylan.spring.aop.anno.After;
import com.ylan.spring.aop.anno.Aspect;
import com.ylan.spring.aop.anno.Before;

/**
 * @author by pepsi-wyl
 * @date 2023-04-23 10:32
 */

@Aspect
@Component
public class PostServiceAspect {

    @Before("execution(* *.PostServiceImpl.*())")
    public void before() {
        System.out.println("post-before 通知....");
    }

    @After("execution(* *.PostServiceImpl.*())")
    public void after() {
        System.out.println("post-after 通知....");
    }

}
