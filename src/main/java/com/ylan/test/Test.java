package com.ylan.test;

import com.ylan.spring.YlanApplicationContext;
import com.ylan.test.Service.OrderService;
import com.ylan.test.Service.UserService;
import com.ylan.test.config.AppConfig;

/**
 * 测试类
 *
 * @author by pepsi-wyl
 * @date 2023-04-19 20:35
 */

public class Test {
    public static void main(String[] args) {
        YlanApplicationContext applicationContext = new YlanApplicationContext(AppConfig.class);

        UserService userService = applicationContext.getBean("userService", UserService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        System.out.println(userService);
        System.out.println(orderService);
    }
}
