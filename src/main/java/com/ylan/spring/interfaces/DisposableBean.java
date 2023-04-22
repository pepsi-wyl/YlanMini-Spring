package com.ylan.spring.interfaces;

/**
 * 对象销毁
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:32
 */

public interface DisposableBean {
    void destroy() throws Exception;
}
