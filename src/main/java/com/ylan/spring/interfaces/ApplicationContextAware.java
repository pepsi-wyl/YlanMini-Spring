package com.ylan.spring.interfaces;

import com.ylan.spring.YlanApplicationContext;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 20:32
 */

public interface ApplicationContextAware {
    void setApplicationContext(YlanApplicationContext applicationContext);
}
