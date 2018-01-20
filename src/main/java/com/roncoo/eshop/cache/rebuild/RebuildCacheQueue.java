package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author: cks
 * @Date: Created by 15:28 2018/1/20
 * @Package: com.roncoo.eshop.cache.queue
 * @Description: 重建缓存的内存队列
 */
public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue(1000);

    public void putQueue(ProductInfo productInfo) {
        try {
            queue.put(productInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ProductInfo takeQueue() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Singleton {
        private static RebuildCacheQueue instance;

        static {
            instance = Singleton.instance;
        }

        public static RebuildCacheQueue getInstance() {
            return Singleton.instance;
        }
    }

    public static RebuildCacheQueue getInstance() {
        return Singleton.instance;
    }

    public static void init() {
        getInstance();
    }
}
