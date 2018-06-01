package com.roncoo.eshop.cache.hystrix;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.model.ProductInfo;

/**
 * @Author: cks
 * @Date: Created by 16:35 2018/5/31
 * @Package: com.roncoo.eshop.cache.hystrix
 * @Description:
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductService"))
//                        .andCommandKey(KEY)
//                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoPool"))
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                .withCoreSize(10)//设置线程池大小
                                .withMaximumSize(30)//可以线程池扩容最大线程大小
                                .withAllowMaximumSizeToDivergeFromCoreSize(true)//允许扩容到30个线程
                                .withKeepAliveTimeMinutes(1)//线程空闲1分钟就给释放了
                                .withMaxQueueSize(50)//设置的是等待缓冲队列的大小
                                .withQueueSizeRejectionThreshold(100))
//                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
//                                .withExecutionTimeoutInMilliseconds(100)
//                                .withCircuitBreakerRequestVolumeThreshold(1000)
//                                .withCircuitBreakerErrorThresholdPercentage(70)
//                                .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
//                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(30))
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() {
        if (productId == 100l){
           ProductInfo productInfo = new ProductInfo();
           productInfo.setId(productId);
           return productInfo;
        }else {
            //发送http rpc接口调用，去调用商品服务的接口
            String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"updateTime\": \"2018-1-20 15:03:13\"}";
            ProductInfo productInfo = JSON.parseObject(productInfoJSON, ProductInfo.class);
            return productInfo;
        }
    }

    @Override
    protected ProductInfo getFallback() {
        HBaseColdDataCommand command = new HBaseColdDataCommand(productId);
        return command.execute();
    }

    private class HBaseColdDataCommand extends HystrixCommand<ProductInfo> {

        private Long productId;

        public HBaseColdDataCommand(Long productId) {
            super(HystrixCommandGroupKey.Factory.asKey("HBaseGroup"));
            this.productId = productId;
        }

        @Override
        protected ProductInfo run() {
            // 查询hbase
            String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:01:00\"}";
            return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        }

        @Override
        protected ProductInfo getFallback() {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(productId);
            // 从内存中找一些残缺的数据拼装进去
            return productInfo;
        }

    }
}
