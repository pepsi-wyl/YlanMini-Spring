package com.ylan.test.aop;

import com.ylan.spring.anno.Component;

/**
 * @author by pepsi-wyl
 * @date 2023-04-23 10:30
 */

@Component("postService")
public class PostServiceImpl implements PostService {
    @Override
    public void post() {
        System.out.println("post........");
    }
}
