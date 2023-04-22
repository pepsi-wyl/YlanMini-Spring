package com.ylan.test.Service;

import com.ylan.spring.anno.Autowired;
import com.ylan.spring.anno.Component;
import com.ylan.spring.anno.Scope;
import com.ylan.spring.interfaces.BeanNameAware;
import com.ylan.spring.interfaces.InitializingBean;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 15:35
 */

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    public OrderService getOrderService() {
        System.out.println(orderService);
        return orderService;
    }

    // BeanNameAware
    @Override
    public void setBeanName(String beanName) {
        System.out.println("[[[[[[   TestMSG   BeanNameAware-setBeanName-设置BeanName:" + this.getClass().getName() + " " + beanName);
    }

    // InitializingBean
    @Override
    public void afterPropertiesSet() {
        System.out.println("[[[[[[   TestMSG   InitializingBean-afterPropertiesSet-初始化:" + this.getClass().getName());
    }
}
