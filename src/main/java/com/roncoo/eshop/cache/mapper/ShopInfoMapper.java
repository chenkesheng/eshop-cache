package com.roncoo.eshop.cache.mapper;

import com.roncoo.eshop.cache.model.ShopInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: cks
 * @Date: Created by 16:19 2018/1/19
 * @Package: com.roncoo.eshop.cache.mapper
 * @Description:
 */
public interface ShopInfoMapper {

    ShopInfo findById(@Param("id") Long id);

}
