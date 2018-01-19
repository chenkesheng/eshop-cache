package com.roncoo.eshop.cache.mapper;

import com.roncoo.eshop.cache.model.ProductInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: cks
 * @Date: Created by 16:52 2018/1/19
 * @Package: com.roncoo.eshop.cache.mapper
 * @Description:
 */
public interface ProductInfoMapper {
    ProductInfo findById(@Param("id") Long id);
}
