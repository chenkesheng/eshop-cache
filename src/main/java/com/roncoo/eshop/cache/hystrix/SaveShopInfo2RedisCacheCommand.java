package com.roncoo.eshop.cache.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

/**
 * @Author: cks
 * @Date: Created by 15:06 2018/5/31
 * @Package: com.roncoo.eshop.cache.hystrix
 * @Description:
 */
public class SaveShopInfo2RedisCacheCommand extends HystrixCommand<Boolean> {

    private ShopInfo shopInfo;

    public SaveShopInfo2RedisCacheCommand(ShopInfo shopInfo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
//                .andCommandKey(KEY)
//                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoPool"))
//                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
//                        .withCoreSize(10)//设置线程池大小
//                        .withMaximumSize(30)//可以线程池扩容最大线程大小
//                        .withAllowMaximumSizeToDivergeFromCoreSize(true)//允许扩容到30个线程
//                        .withKeepAliveTimeMinutes(1)//线程空闲1分钟就给释放了
//                        .withMaxQueueSize(12)//设置的是等待缓冲队列的大小
//                        .withQueueSizeRejectionThreshold(15))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(100)
                                .withCircuitBreakerRequestVolumeThreshold(1000)
                                .withCircuitBreakerErrorThresholdPercentage(70)
                                .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
//                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(30))
                ));
        this.shopInfo = shopInfo;
    }

    @Override
    protected Boolean run() {
        JedisCluster jedisCluster = (JedisCluster) SpringContext.getApplicationContext().getBean("JedisClusterFactory");
        String key = "shop_info_" + shopInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
