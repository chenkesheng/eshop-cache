package com.roncoo.eshop.cache.service.impl;

import javax.annotation.Resource;

import com.roncoo.eshop.cache.hystrix.GetProductInfoFromRedisCacheCommand;
import com.roncoo.eshop.cache.hystrix.GetShopInfoFromRedisCacheCommand;
import com.roncoo.eshop.cache.hystrix.SaveProductInfo2RedisCacheCommand;
import com.roncoo.eshop.cache.hystrix.SaveShopInfo2RedisCacheCommand;
import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.mapper.ShopInfoMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;

/**
 * @Author: cks
 * @Date: Created by 16:52 2018/1/19
 * @Package: com.roncoo.eshop.cache.service.impl
 * @Description: 缓存service实现
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    @Resource
    private JedisCluster jedisCluster;
    @Resource
    private ShopInfoMapper shopInfoMapper;
    @Resource
    private ProductInfoMapper productInfoMapper;

    /**
     * 将商品信息保存到本地缓存中
     *
     * @param productInfo
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
    public ProductInfo saveLocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    /**
     * 从本地缓存中获取商品信息
     *
     * @param id
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
    public ProductInfo getLocalCache(Long id) {
        return null;
    }

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productInfo
     */
    @CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     */
    @CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    /**
     * 从本地ehcache缓存中获取店铺信息
     *
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }

    /**
     * 将商品信息保存到redis中
     *
     * @param productInfo
     */
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
//        String key = "product_info_" + productInfo.getId();
//        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        //给redis集成hystrix 进行资源隔离，降级等处理
        SaveProductInfo2RedisCacheCommand command = new SaveProductInfo2RedisCacheCommand(productInfo);
        command.execute();
    }

    /**
     * 将店铺信息保存到redis中
     */
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
//        String key = "shop_info_" + shopInfo.getId();
//        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        SaveShopInfo2RedisCacheCommand cacheCommand = new SaveShopInfo2RedisCacheCommand(shopInfo);
        cacheCommand.execute();
    }

    /**
     * 从redis中获取商品信息
     */
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
//        String key = "product_info_" + productId;
//        String json = jedisCluster.get(key);
//        if (null != json) {
//            return JSONObject.parseObject(json, ProductInfo.class);
//        }
        GetProductInfoFromRedisCacheCommand cacheCommand = new GetProductInfoFromRedisCacheCommand(productId);
        return cacheCommand.execute();
    }

    /**
     * 从redis中获取店铺信息
     */
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
//        String key = "shop_info_" + shopId;
//        String json = jedisCluster.get(key);
//        if (null != json) {
//            return JSONObject.parseObject(json, ShopInfo.class);
//        }
        GetShopInfoFromRedisCacheCommand command = new GetShopInfoFromRedisCacheCommand(shopId);
        return command.execute();
    }

    @Override
    public ProductInfo findByProductId(Long productId) {
        String key = "product_info_" + productId;
        ProductInfo productInfo = productInfoMapper.findById(productId);
//        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        return productInfo;
    }

    @Override
//    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    public ShopInfo finByShopId(Long id) {
        String key = "shop_info_" + id;
        ShopInfo shopInfo = shopInfoMapper.findById(id);
//        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        return shopInfo;
    }

}
