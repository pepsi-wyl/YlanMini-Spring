package com.ylan.test;

import com.ylan.spring.YlanApplicationContext;

/**
 * 测试类
 * @author by pepsi-wyl
 * @date 2023-04-19 20:35
 */

public class Test {
    public static void main(String[] args) {
        YlanApplicationContext applicationContext = new YlanApplicationContext(AppConfig.class);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));

        System.out.println(applicationContext.getBean("test1"));
        System.out.println(applicationContext.getBean("test1"));
    }
}
