package com.java1234.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.Notice;

/**
 * 公告资讯服务接口
 *
 * @author Java1234
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告
     *
     * @param page    分页对象
     * @param keyword 关键字
     * @param type    类型
     * @return 分页结果
     */
    Page<Notice> pageNotice(Page<Notice> page, String keyword, String type);

    /**
     * 查询公告详情并累加阅读量
     *
     * @param id 公告ID
     * @return 公告详情
     */
    Notice getDetail(Long id);
}
