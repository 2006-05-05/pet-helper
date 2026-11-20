package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.common.BusinessException;
import com.java1234.entity.Notice;
import com.java1234.mapper.NoticeMapper;
import com.java1234.service.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 公告资讯服务实现类
 *
 * @author Java1234
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    /**
     * 分页查询公告
     *
     * @param page    分页对象
     * @param keyword 关键字
     * @param type    类型
     * @return 分页结果
     */
    @Override
    public Page<Notice> pageNotice(Page<Notice> page, String keyword, String type) {
        LambdaQueryWrapper<Notice> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Notice::getTitle, keyword);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Notice::getType, type);
        }
        wrapper.orderByDesc(Notice::getCreateTime).orderByDesc(Notice::getId);
        return this.page(page, wrapper);
    }

    /**
     * 查询公告详情并累加阅读量
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @Override
    public Notice getDetail(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在或已删除");
        }
        // 阅读量加一
        Notice update = new Notice();
        update.setId(id);
        update.setViewCount((notice.getViewCount() == null ? 0 : notice.getViewCount()) + 1);
        this.updateById(update);
        notice.setViewCount(update.getViewCount());
        return notice;
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 公告ID
     */
    public Long addNotice(Notice notice) {
        notice.setId(null);
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        notice.setViewCount(0);
        notice.setCreateTime(LocalDateTime.now());
        this.save(notice);
        return notice.getId();
    }
}
