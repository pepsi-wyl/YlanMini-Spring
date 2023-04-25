package com.ylan.test.aop;

import com.ylan.spring.anno.Component;
import com.ylan.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.ylan.spring.aop.anno.*;

/**
 * 环绕前  before  方法体  afterReturning/afterThrowing  after  环绕后
 *
 * @author by pepsi-wyl
 * @date 2023-04-23 10:32
 */

@Aspect
@Component
public class PostServiceAspect {

    // 前置通知
//    @Before("execution(* *.PostServiceImpl.*())")
    public void before1() {
        System.out.println("post before1-通知....");
    }

    // 前置通知
//    @Before("execution(* *.PostServiceImpl.*())")
    public void before2() {
        System.out.println("post before2-通知....");
    }

    // 异常通知
//    @AfterThrowing("execution(* *.PostServiceImpl.*())")
    public void afterThrowing() {
        System.out.println("post afterThrowing-通知....");
    }

    // 后置通知(返回通知)  异常出现不执行
//    @AfterReturning("execution(* *.PostServiceImpl.*())")
    public void afterReturning1() {
        System.out.println("post afterReturning1-通知....");
    }

    // 后置通知(返回通知)  异常出现不执行
//    @AfterReturning("execution(* *.PostServiceImpl.*())")
    public void afterReturning2() {
        System.out.println("post afterReturning2-通知....");
    }

    // 最终通知      异常出现也执行
//    @After("execution(* *.PostServiceImpl.*())")
    public void after1() {
        System.out.println("post after1-通知....");
    }

    // 最终通知      异常出现也执行
//    @After("execution(* *.PostServiceImpl.*())")
    public void after2() {
        System.out.println("post after2-通知....");
    }

    // 环绕通知
    @Around("execution(* *.PostServiceImpl.*())")
    public void around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("post around前-通知....");
        point.proceed();    // 相当于过滤
        System.out.println("post around后-通知....");
    }
}