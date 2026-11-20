package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
