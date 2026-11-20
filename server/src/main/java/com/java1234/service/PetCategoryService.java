package com.java1234.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.PetCategory;

import java.util.List;

/**
 * 宠物分类服务接口
 *
 * @author Java1234
 */
public interface PetCategoryService extends IService<PetCategory> {

    /**
     * 查询全部分类（按排序升序）
     *
     * @return 分类列表
     */
    List<PetCategory> listAll();
}
