package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.entity.PetCategory;
import com.java1234.mapper.PetCategoryMapper;
import com.java1234.service.PetCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物分类服务实现类
 *
 * @author Java1234
 */
@Service
public class PetCategoryServiceImpl extends ServiceImpl<PetCategoryMapper, PetCategory> implements PetCategoryService {

    /**
     * 查询全部分类（按排序升序）
     *
     * @return 分类列表
     */
    @Override
    public List<PetCategory> listAll() {
        return this.list(Wrappers.<PetCategory>lambdaQuery()
                .orderByAsc(PetCategory::getSort)
                .orderByAsc(PetCategory::getId));
    }
}
