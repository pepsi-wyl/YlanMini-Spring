package com.ylan.test.Service;

import com.ylan.spring.anno.Autowired;
import com.ylan.spring.anno.Component;
import com.ylan.spring.anno.Scope;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 15:35
 */

@Component("userService")
@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;


}
