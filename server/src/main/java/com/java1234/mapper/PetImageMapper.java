package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.PetImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物相册数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface PetImageMapper extends BaseMapper<PetImage> {
}
