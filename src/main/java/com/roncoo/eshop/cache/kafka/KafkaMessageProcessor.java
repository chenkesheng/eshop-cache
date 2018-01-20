package com.roncoo.eshop.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;

import com.roncoo.eshop.cache.zk.ZookeeperSession;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: cks
 * @Date: Created by 16:52 2018/1/19
 * @Package: com.roncoo.eshop.cache.kafka
 * @Description: kafka消息处理线程
 */
@SuppressWarnings("rawtypes")
public class KafkaMessageProcessor implements Runnable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private KafkaStream kafkaStream;
    private CacheService cacheService;

    public KafkaMessageProcessor(KafkaStream kafkaStream) {
        this.kafkaStream = kafkaStream;
        this.cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");
    }

    @SuppressWarnings("unchecked")
    public void run() {
        ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        while (it.hasNext()) {
            String message = new String(it.next().message());

            // 首先将message转换成json对象
            JSONObject messageJSONObject = JSONObject.parseObject(message);

            // 从这里提取出消息对应的服务的标识
            String serviceId = messageJSONObject.getString("serviceId");

            // 如果是商品信息服务
            if ("productInfoService".equals(serviceId)) {
                processProductInfoChangeMessage(messageJSONObject);
            } else if ("shopInfoService".equals(serviceId)) {
                processShopInfoChangeMessage(messageJSONObject);
            }
        }
    }

    /**
     * 处理商品信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来
        String productInfoJSON = "{\"id\": 2, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"update_time\": \"2018-1-20 15:03:03\"}";
        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

        //在更新redis数据之前先进行获取分布式锁
        ZookeeperSession zookeeperSession = ZookeeperSession.getInstance();
        zookeeperSession.acquireDistributedLock(productId);

        //已经获取到了锁
        //从redis中获取数据
        ProductInfo exitedProductInfo = cacheService.getProductInfoFromRedisCache(productId);
        if (null != exitedProductInfo) {
            try {
                Date date = sdf.parse(productInfo.getUpdateTime());
                Date exitedDate = sdf.parse(exitedProductInfo.getUpdateTime());
                if (date.before(exitedDate)) {
                    System.out.println("要更新的数据时间在redis中已经存在的数据时间之前" + productInfo.getUpdateTime() + "所以不需要更新");
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println("exited product info is null or update time after exitedDate");
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cacheService.saveProductInfo2LocalCache(productInfo);
        System.out.println("===================获取刚保存到本地缓存的商品信息：" + cacheService.getProductInfoFromLocalCache(productId));
        cacheService.saveProductInfo2RedisCache(productInfo);
        //释放zk分布式锁
        zookeeperSession.releaseDistributedLock(productId);
    }

    /**
     * 处理店铺信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

        String shopInfoJSON = "{\"id\": 1, \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        System.out.println("===================获取刚保存到本地缓存的店铺信息：" + cacheService.getShopInfoFromLocalCache(shopId));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }

}
