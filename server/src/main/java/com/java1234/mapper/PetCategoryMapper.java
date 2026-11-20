package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.PetCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物分类数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface PetCategoryMapper extends BaseMapper<PetCategory> {
}
