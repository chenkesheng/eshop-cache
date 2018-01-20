package com.roncoo.eshop.cache.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.roncoo.eshop.cache.rebuild.RebuildCacheThread;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.roncoo.eshop.cache.kafka.KafkaConsumer;
import com.roncoo.eshop.cache.spring.SpringContext;

/**
 * @Author: cks
 * @Date: Created by 16:19 2018/1/19
 * @Package: com.roncoo.eshop.cache.listener
 * @Description: 系统初始化监听器
 */
public class InitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        new Thread(new KafkaConsumer("cache-message")).start();

        new  Thread(new RebuildCacheThread()).start();
        //zk分布式锁进行初始化
        ZookeeperSession.init();


    }

    public void contextDestroyed(ServletContextEvent sce) {

    }

}
