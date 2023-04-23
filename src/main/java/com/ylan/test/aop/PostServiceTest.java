package com.ylan.test.aop;

import com.ylan.spring.YlanApplicationContext;
import com.ylan.test.config.AppConfig;

/**
 * @author by pepsi-wyl
 * @date 2023-04-23 10:34
 */

public class PostServiceTest {
    public static void main(String[] args) {
        YlanApplicationContext applicationContext = new YlanApplicationContext(AppConfig.class);
        PostService postService = applicationContext.getBean("postService", PostService.class);
        postService.post();
    }
}
