package com.ylan.test.circularReferences;

import com.ylan.spring.YlanApplicationContext;
import com.ylan.test.config.AppConfig;

/**
 * @author by pepsi-wyl
 * @date 2023-04-22 10:09
 */

public class TestAB {
    public static void main(String[] args) {
        YlanApplicationContext applicationContext = new YlanApplicationContext(AppConfig.class);

        A a = applicationContext.getBean("a", A.class);
        B b = applicationContext.getBean("b", B.class);

        applicationContext.destroyBean("a", a);

        A a1 = applicationContext.getBean("a", A.class);
        B b1 = applicationContext.getBean("b", B.class);

        applicationContext.close();
    }
}
