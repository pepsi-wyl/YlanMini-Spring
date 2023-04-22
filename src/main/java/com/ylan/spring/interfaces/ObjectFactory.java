package com.ylan.spring.interfaces;

/**
 * 三级缓存 对象工厂
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:16
 */

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}
