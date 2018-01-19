package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;

/**
 * @Author: cks
 * @Date: Created by 16:52 2018/1/19
 * @Package: com.roncoo.eshop.cache.service
 * @Description: 缓存service接口
 */
public interface CacheService {

    /**
     * 将商品信息保存到本地缓存中
     *
     * @param productInfo
     * @return
     */
    ProductInfo saveLocalCache(ProductInfo productInfo);

    /**
     * 从本地缓存中获取商品信息
     *
     * @param id
     * @return
     */
    ProductInfo getLocalCache(Long id);

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productInfo
     */
    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    ProductInfo getProductInfoFromLocalCache(Long productId);

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     */
    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    /**
     * 从本地ehcache缓存中获取店铺信息
     *
     * @return
     */
    ShopInfo getShopInfoFromLocalCache(Long shopId);

    /**
     * 将商品信息保存到redis中
     *
     * @param productInfo
     */
    void saveProductInfo2RedisCache(ProductInfo productInfo);

    /**
     * 将店铺信息保存到redis中
     */
    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    /**
     * 从redis中获取商品信息
     */
    ProductInfo getProductInfoFromRedisCache(Long productId);

    /**
     * 从redis中获取店铺信息
     */
    ShopInfo getShopInfoFromRedisCache(Long shopId);

    /**
     * 从mysql中获取商品信息
     *
     * @param productId
     * @return
     */
    ProductInfo findByProductId(Long productId);

    /**
     * 从mysql中获取店铺信息
     */
    ShopInfo finByShopId(Long id);

}
