package com.java1234.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.entity.Notice;
import com.java1234.service.NoticeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 公告资讯控制器
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    /** 公告服务 */
    private final NoticeService noticeService;

    /**
     * 构造方法，注入依赖
     *
     * @param noticeService 公告服务
     */
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 分页查询公告列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  关键字
     * @param type     类型
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<Notice>> list(@RequestParam(defaultValue = "1") Long page,
                                           @RequestParam(defaultValue = "10") Long pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String type) {
        Page<Notice> pageInfo = new Page<>(page, pageSize);
        Page<Notice> result = noticeService.pageNotice(pageInfo, keyword, type);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 查询公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<Notice> detail(@PathVariable Long id) {
        return Result.success(noticeService.getDetail(id));
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 公告ID
     */
    @PostMapping
    public Result<Long> add(@RequestBody Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().isEmpty()) {
            throw new com.java1234.common.BusinessException("请输入公告标题");
        }
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        notice.setViewCount(0);
        notice.setCreateTime(LocalDateTime.now());
        notice.setId(null);
        noticeService.save(notice);
        return Result.success("新增成功", notice.getId());
    }

    /**
     * 修改公告
     *
     * @param id     公告ID
     * @param notice 公告信息
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.updateById(notice);
        return Result.success("修改成功", null);
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("删除成功", null);
    }
}
