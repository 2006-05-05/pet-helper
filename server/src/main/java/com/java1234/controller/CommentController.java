package com.java1234.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.LoginUser;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.entity.Comment;
import com.java1234.entity.SysUser;
import com.java1234.service.CommentService;
import com.java1234.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 留言评论控制器
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    /** 留言服务 */
    private final CommentService commentService;

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造方法，注入依赖
     *
     * @param commentService 留言服务
     * @param sysUserService 用户服务
     */
    public CommentController(CommentService commentService, SysUserService sysUserService) {
        this.commentService = commentService;
        this.sysUserService = sysUserService;
    }

    /**
     * 后台分页查询留言
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  关键字
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<Comment>> page(@RequestParam(defaultValue = "1") Long page,
                                            @RequestParam(defaultValue = "10") Long pageSize,
                                            @RequestParam(required = false) String keyword) {
        Page<Comment> pageInfo = new Page<>(page, pageSize);
        Page<Comment> result = commentService.pageComment(pageInfo, null, keyword);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 查询某宠物的留言列表
     *
     * @param petId 宠物ID
     * @return 留言列表
     */
    @GetMapping("/list/{petId}")
    public Result<List<Comment>> list(@PathVariable Long petId) {
        return Result.success(commentService.listByPet(petId));
    }

    /**
     * 新增留言
     *
     * @param comment 留言信息
     * @return 留言ID
     */
    @PostMapping
    public Result<Long> add(@RequestBody Comment comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new com.java1234.common.BusinessException("请输入留言内容");
        }
        SysUser user = sysUserService.getByUsername(LoginUser.getUsername());
        if (user == null) {
            throw new com.java1234.common.BusinessException("登录状态已失效，请重新登录");
        }
        comment.setUserId(user.getId());
        comment.setStatus(1);
        comment.setCreateTime(java.time.LocalDateTime.now());
        comment.setId(null);
        commentService.save(comment);
        return Result.success("留言成功", comment.getId());
    }

    /**
     * 删除留言
     *
     * @param id 留言ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success("删除成功", null);
    }
}
