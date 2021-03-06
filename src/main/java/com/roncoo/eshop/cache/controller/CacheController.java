package com.roncoo.eshop.cache.controller;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.roncoo.eshop.cache.hystrix.GetProductInfoCommand;
import com.roncoo.eshop.cache.prewarm.CachePreWarmThread;
import com.roncoo.eshop.cache.rebuild.RebuildCacheQueue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;

/**
 * @Author: cks
 * @Date: Created by 16:52 2018/1/19
 * @Package: com.roncoo.eshop.cache.controller
 * @Description: 缓存controller
 */
@Controller
public class CacheController {

    @Resource
    private CacheService cacheService;

    @RequestMapping("/testPutCache")
    @ResponseBody
    public String testPutCache(ProductInfo productInfo) {
        cacheService.saveLocalCache(productInfo);
        return "success";
    }

    @RequestMapping("/testGetCache")
    @ResponseBody
    public ProductInfo testGetCache(Long id) {
        return cacheService.getLocalCache(id);
    }

    @RequestMapping("/getProductInfo")
    @ResponseBody
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfo;

        productInfo = cacheService.getProductInfoFromRedisCache(productId);
        if(productInfo != null){
            System.out.println("=================从redis中获取缓存，商品信息=" + productInfo);
        }

        if (productInfo == null) {
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            if(productInfo != null){
                System.out.println("=================从ehcache中获取缓存，商品信息=" + productInfo);
            }
        }

        if (productInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存
//            productInfo = cacheService.findByProductId(productId);
//            String productInfoJSON = "{\"id\": "+productId+", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"updateTime\": \"2018-1-20 15:03:13\"}";
//            productInfo=JSON.parseObject(productInfoJSON,ProductInfo.class);

            GetProductInfoCommand command = new GetProductInfoCommand(productId);
            productInfo = command.execute();
            //将数据推到内存队列中去重构缓存
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.putQueue(productInfo);

        }

        return productInfo;
    }

    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Long shopId) {
        ShopInfo shopInfo = null;

        shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
        System.out.println("=================从redis中获取缓存，店铺信息=" + shopInfo);

        if (shopInfo == null) {
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
            System.out.println("=================从ehcache中获取缓存，店铺信息=" + shopInfo);
        }

        if (shopInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存
            shopInfo = cacheService.finByShopId(shopId);
        }

        return shopInfo;
    }

    @RequestMapping("/preWarmCache")
    @ResponseBody
    public void preWarmCache() {
        new CachePreWarmThread().start();
    }

}
