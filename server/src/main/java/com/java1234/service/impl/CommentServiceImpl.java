package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.entity.Comment;
import com.java1234.entity.SysUser;
import com.java1234.mapper.CommentMapper;
import com.java1234.mapper.SysUserMapper;
import com.java1234.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 留言评论服务实现类
 *
 * @author Java1234
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    /** 用户数据访问对象 */
    private final SysUserMapper sysUserMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param sysUserMapper 用户数据访问对象
     */
    public CommentServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 分页查询留言
     *
     * @param page    分页对象
     * @param petId   宠物ID
     * @param keyword 关键字
     * @return 分页结果
     */
    @Override
    public Page<Comment> pageComment(Page<Comment> page, Long petId, String keyword) {
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        if (petId != null) {
            wrapper.eq(Comment::getPetId, petId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime).orderByDesc(Comment::getId);
        Page<Comment> result = this.page(page, wrapper);
        this.fillUser(result.getRecords());
        return result;
    }

    /**
     * 查询某宠物的留言列表
     *
     * @param petId 宠物ID
     * @return 留言列表
     */
    @Override
    public List<Comment> listByPet(Long petId) {
        List<Comment> list = this.list(Wrappers.<Comment>lambdaQuery()
                .eq(Comment::getPetId, petId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime));
        this.fillUser(list);
        return list;
    }

    /**
     * 新增留言
     *
     * @param comment 留言信息
     * @return 留言ID
     */
    public Long addComment(Comment comment) {
        comment.setId(null);
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        this.save(comment);
        return comment.getId();
    }

    /**
     * 填充留言人的昵称与头像
     *
     * @param comments 留言列表
     */
    private void fillUser(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        List<Long> userIds = comments.stream().map(Comment::getUserId).filter(Objects::nonNull).distinct().toList();
        if (userIds.isEmpty()) {
            return;
        }
        Map<Long, SysUser> userMap = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, userIds)).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
        for (Comment comment : comments) {
            SysUser user = userMap.get(comment.getUserId());
            if (user != null) {
                comment.setNickname(user.getNickname());
                comment.setAvatar(user.getAvatar());
            }
        }
    }
}
