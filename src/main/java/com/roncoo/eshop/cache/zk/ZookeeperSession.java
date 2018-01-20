package com.roncoo.eshop.cache.zk;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: cks
 * @Date: Created by 14:16 2018/1/20
 * @Package: com.roncoo.eshop.cache.zk
 * @Description: ZookeeperSession
 */
public class ZookeeperSession {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    public ZookeeperSession() {
        // 去连接zookeeper server，创建会话的时候，是异步去进行的
        // 所以要给一个监听器，告诉我们什么时候才是真正完成了跟zk server的连接
        try {
            this.zooKeeper = new ZooKeeper("192.168.30.100,192.168.30.102,192.168.30.103",
                    40000, new ZookeeperWatcher());

            //给个状态叫做connecting连接中
            System.out.println(zooKeeper.getState());

            try {
                //CountDownLatch 是java多线程并发同步的一个工具类
                // 会传递进去一些数字，比如说1,2 ，3 都可以
                // 然后await()，如果数字不是0，那么久卡住，等待

                // 其他的线程可以调用CountDown()，减1
                // 如果数字减到0，那么之前所有在await的线程，都会逃出阻塞的状态
                // 继续向下运行
                connectedSemaphore.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ZooKeeper session established......");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分布式锁
     *
     * @param productId
     */
    public void acquireDistributedLock(Long productId) {
        //尝试对过来的请求进行加锁
        String path = "/product-lock-" + productId;
        try {                      //给个空数据      不建立任何权限                 创建临时节点
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for product[id=" + productId + "]");
        } catch (Exception e) {
            // 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
            // NodeExistsException
            e.printStackTrace();
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(20);
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e1) {
//                    e1.printStackTrace();
                    count++;
                    System.out.println("the" + count + "times try to acquire lock for product id = " + productId);
                    continue;
                }
                System.out.println("success to acquire lock for product[id=" + productId + "] after " + count + " times try......");
                break;
            }
        }
    }

    /**
     * 释放分布式锁
     *
     * @param productId
     */
    public void releaseDistributedLock(Long productId) {
        String path = "/product-lock-" + productId;
        try {
            zooKeeper.delete(path, -1);
            System.out.println("release the lock for product id =" + productId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    private class ZookeeperWatcher implements Watcher {
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("Receive watched event: " + watchedEvent.getState());
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                connectedSemaphore.countDown();
            }
        }
    }

    /**
     * 封装单例的zk内部类
     */
    private static class Singleton {
        private static ZookeeperSession instance;

        static {
            instance = new ZookeeperSession();
        }

        public static ZookeeperSession getInstance() {
            return instance;
        }
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static ZookeeperSession getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 初始化单例的方法
     */
    public static void init() {
        getInstance();
    }
}
