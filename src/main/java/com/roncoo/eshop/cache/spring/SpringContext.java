package com.roncoo.eshop.cache.spring;

import org.springframework.context.ApplicationContext;

/**
 * @Author: cks
 * @Date: Created by 16:19 2018/1/19
 * @Package: com.roncoo.eshop.cache.spring
 * @Description: spring上下文
 */
public class SpringContext {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }

}
