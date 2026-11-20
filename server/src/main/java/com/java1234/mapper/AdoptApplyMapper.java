package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.AdoptApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 领养申请数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface AdoptApplyMapper extends BaseMapper<AdoptApply> {
}
