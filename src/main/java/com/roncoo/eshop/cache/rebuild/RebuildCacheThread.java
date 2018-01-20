package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: cks
 * @Date: Created by 15:49 2018/1/20
 * @Package: com.roncoo.eshop.cache.rebuild
 * @Description: 重建缓存的线程
 */
public class RebuildCacheThread implements Runnable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    RebuildCacheQueue queue = RebuildCacheQueue.getInstance();

    ZookeeperSession zkSession = ZookeeperSession.getInstance();

    CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

    @Override
    public void run() {
        while (true) {
            ProductInfo productInfo = queue.takeQueue();
            //获取分布式锁
            zkSession.acquireDistributedLock(productInfo.getId());
            //已经获取到了锁
            //从redis中获取数据
            ProductInfo exitedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());
            if (null != exitedProductInfo) {
                try {
                    Date date = sdf.parse(productInfo.getUpdateTime());
                    Date exitedDate = sdf.parse(exitedProductInfo.getUpdateTime());
                    if (date.before(exitedDate)) {
                        System.out.println("要更新的数据时间在redis中已经存在的数据时间之前" + productInfo.getUpdateTime() + "所以不需要更新");
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("exited product info is null or update time after exitedDate");
            cacheService.saveProductInfo2LocalCache(productInfo);
            cacheService.saveProductInfo2RedisCache(productInfo);
            //释放zk分布式锁
            zkSession.releaseDistributedLock(productInfo.getId());
        }

    }
}
