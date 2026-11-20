package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告资讯数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
