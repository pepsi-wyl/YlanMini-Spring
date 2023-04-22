package com.ylan.test.circularReferences;

import com.ylan.spring.YlanApplicationContext;
import com.ylan.test.config.AppConfig;

/**
 * @author by pepsi-wyl
 * @date 2023-04-22 10:54
 */
public class TestCD {
    public static void main(String[] args) {
        YlanApplicationContext applicationContext = new YlanApplicationContext(AppConfig.class);

        C c = applicationContext.getBean("c", C.class);
        D d = applicationContext.getBean("d", D.class);

    }
}
