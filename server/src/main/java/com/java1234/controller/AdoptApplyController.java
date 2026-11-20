package com.java1234.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.LoginUser;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.dto.ApplyDTO;
import com.java1234.entity.AdoptApply;
import com.java1234.entity.SysUser;
import com.java1234.service.AdoptApplyService;
import com.java1234.service.SysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 领养申请控制器
 *
 * <p>包含后台审核接口与小程序端提交、查询、取消接口。</p>
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/apply")
public class AdoptApplyController {

    /** 领养申请服务 */
    private final AdoptApplyService adoptApplyService;

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造方法，注入依赖
     *
     * @param adoptApplyService 领养申请服务
     * @param sysUserService    用户服务
     */
    public AdoptApplyController(AdoptApplyService adoptApplyService, SysUserService sysUserService) {
        this.adoptApplyService = adoptApplyService;
        this.sysUserService = sysUserService;
    }

    /**
     * 后台分页查询领养申请
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param status   审核状态
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<AdoptApply>> page(@RequestParam(defaultValue = "1") Long page,
                                               @RequestParam(defaultValue = "10") Long pageSize,
                                               @RequestParam(required = false) Integer status) {
        Page<AdoptApply> pageInfo = new Page<>(page, pageSize);
        Page<AdoptApply> result = adoptApplyService.pageApply(pageInfo, status, null);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 审核领养申请
     *
     * @param id     申请ID
     * @param status 审核状态：1 通过 2 驳回
     * @param remark 审核备注
     * @return 是否成功
     */
    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestParam Integer status,
                              @RequestParam(required = false) String remark) {
        adoptApplyService.audit(id, status, remark);
        return Result.success(status == 1 ? "审核通过" : "已驳回", null);
    }

    /**
     * 提交领养申请
     *
     * @param applyDTO 申请信息
     * @return 申请ID
     */
    @PostMapping
    public Result<Long> submit(@RequestBody ApplyDTO applyDTO) {
        SysUser user = currentUser();
        AdoptApply apply = new AdoptApply();
        apply.setPetId(applyDTO.getPetId());
        apply.setUserId(user.getId());
        apply.setReason(applyDTO.getReason());
        apply.setAddress(applyDTO.getAddress());
        apply.setPhone(applyDTO.getPhone() == null ? user.getPhone() : applyDTO.getPhone());
        apply.setExperience(applyDTO.getExperience());
        Long id = adoptApplyService.submitApply(apply);
        return Result.success("提交成功，请等待审核", id);
    }

    /**
     * 查询我的领养申请列表
     *
     * @return 申请列表
     */
    @GetMapping("/my")
    public Result<List<AdoptApply>> my() {
        SysUser user = currentUser();
        return Result.success(adoptApplyService.listByUser(user.getId()));
    }

    /**
     * 取消领养申请
     *
     * @param id 申请ID
     * @return 是否成功
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        SysUser user = currentUser();
        adoptApplyService.cancel(id, user.getId());
        return Result.success("已取消申请", null);
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前用户
     */
    private SysUser currentUser() {
        SysUser user = sysUserService.getByUsername(LoginUser.getUsername());
        if (user == null) {
            throw new com.java1234.common.BusinessException("登录状态已失效，请重新登录");
        }
        return user;
    }
}
