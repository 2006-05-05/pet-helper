package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言评论数据访问接口
 *
 * @author Java1234
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
