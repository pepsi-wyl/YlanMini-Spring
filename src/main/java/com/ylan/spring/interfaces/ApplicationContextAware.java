package com.ylan.spring.interfaces;

import com.ylan.spring.YlanApplicationContext;

/**
 * 将 applicationContext 传递给 bean  某个bean 实现了这个接口，就能得到它的 applicationContext
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:32
 */

public interface ApplicationContextAware {
    void setApplicationContext(YlanApplicationContext applicationContext);
}
