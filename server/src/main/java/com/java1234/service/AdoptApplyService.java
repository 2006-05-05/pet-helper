package com.java1234.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.AdoptApply;

import java.util.List;

/**
 * 领养申请服务接口
 *
 * @author Java1234
 */
public interface AdoptApplyService extends IService<AdoptApply> {

    /**
     * 分页查询领养申请（后台管理用）
     *
     * @param page     分页对象
     * @param status   审核状态
     * @param keyword  关键字（宠物名或申请人）
     * @return 分页结果
     */
    Page<AdoptApply> pageApply(Page<AdoptApply> page, Integer status, String keyword);

    /**
     * 查询某用户的申请列表
     *
     * @param userId 用户ID
     * @return 申请列表
     */
    List<AdoptApply> listByUser(Long userId);

    /**
     * 提交领养申请
     *
     * @param apply 申请信息
     * @return 申请ID
     */
    Long submitApply(AdoptApply apply);

    /**
     * 审核领养申请
     *
     * @param id          申请ID
     * @param status      审核结果：1 通过 2 驳回
     * @param auditRemark 审核备注
     * @return 是否成功
     */
    boolean audit(Long id, Integer status, String auditRemark);

    /**
     * 取消领养申请
     *
     * @param id     申请ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean cancel(Long id, Long userId);
}
