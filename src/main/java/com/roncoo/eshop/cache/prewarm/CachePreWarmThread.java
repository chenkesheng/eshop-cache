package com.roncoo.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;

/**
 * @Author: cks
 * @Date: Created by 16:25 2018/1/25
 * @Package: com.roncoo.eshop.cache.prewarm
 * @Description: 缓存预热线程
 */
public class CachePreWarmThread extends Thread {

    public void run() {
        CacheService cacheService = (CacheService) SpringContext.
                getApplicationContext().getBean("cacheService");
        ZookeeperSession zkSession = ZookeeperSession.getInstance();

        //获取taskid列表
        String taskidList = zkSession.getNodeData("/taskid-list");
        System.out.println("【CachePreWarmThread获取到taskid列表】taskidList=" + taskidList);

        if (taskidList != null && !"".equals(taskidList)) {
            String[] taskidListSplited = taskidList.split(",");
            for (String taskid : taskidListSplited) {
                String taskidLockPath = "/taskid-lock" + taskid;
                boolean result = zkSession.acquireFastFailedDistributedLock(taskidLockPath);
                if (!result) {
                    continue;
                }
                String taskidStatusLockPath = "taskid-status-lock" + taskid;
                zkSession.acquireDistributedLock(taskidStatusLockPath);

                String taskidStatus = zkSession.getNodeData("taskid-status" + taskid);
                System.out.println("【获取CachePreWarmThread的task预热状态】taskid="+taskid);
                if ("".equals(taskidStatus)) {
                    String productIdList = zkSession.getNodeData("task-hot-product-list" + taskid+",status="+taskidStatus);
                    System.out.println("【CachePreWarmThread获取到的热门商品列表】productIdList="+productIdList);
                    JSONArray productJSONArray = JSONArray.parseArray(productIdList);
                    for (int i = 0; i < productJSONArray.size(); i++) {
                        Long productId = productJSONArray.getLong(i);
                        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                        cacheService.saveProductInfo2LocalCache(productInfo);
                        System.out.println("【CachePreWarmThread将商品数据设置到本地缓存中】productInfo="+productInfo);
                        cacheService.saveProductInfo2RedisCache(productInfo);
                        System.out.println("【CachePreWarmThread将商品数据设置到redis缓存中】productInfo="+productInfo);
                    }
                    zkSession.setNodeData(taskidStatusLockPath, "success");
                }

                zkSession.releaseDistributedLock(taskidStatusLockPath);
                zkSession.releaseDistributedLock(taskidLockPath);
            }
        }
    }
}
