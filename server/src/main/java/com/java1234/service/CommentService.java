package com.java1234.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.Comment;

import java.util.List;

/**
 * 留言评论服务接口
 *
 * @author Java1234
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询留言
     *
     * @param page    分页对象
     * @param petId   宠物ID
     * @param keyword 关键字
     * @return 分页结果
     */
    Page<Comment> pageComment(Page<Comment> page, Long petId, String keyword);

    /**
     * 查询某宠物的留言列表
     *
     * @param petId 宠物ID
     * @return 留言列表
     */
    List<Comment> listByPet(Long petId);
}
