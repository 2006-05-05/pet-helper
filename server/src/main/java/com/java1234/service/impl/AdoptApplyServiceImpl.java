package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.common.BusinessException;
import com.java1234.entity.AdoptApply;
import com.java1234.entity.Pet;
import com.java1234.entity.SysUser;
import com.java1234.mapper.AdoptApplyMapper;
import com.java1234.mapper.PetMapper;
import com.java1234.mapper.SysUserMapper;
import com.java1234.service.AdoptApplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 领养申请服务实现类
 *
 * @author Java1234
 */
@Service
public class AdoptApplyServiceImpl extends ServiceImpl<AdoptApplyMapper, AdoptApply> implements AdoptApplyService {

    /** 宠物数据访问对象 */
    private final PetMapper petMapper;

    /** 用户数据访问对象 */
    private final SysUserMapper sysUserMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param petMapper     宠物数据访问对象
     * @param sysUserMapper 用户数据访问对象
     */
    public AdoptApplyServiceImpl(PetMapper petMapper, SysUserMapper sysUserMapper) {
        this.petMapper = petMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 分页查询领养申请
     *
     * @param page    分页对象
     * @param status  审核状态
     * @param keyword 关键字
     * @return 分页结果
     */
    @Override
    public Page<AdoptApply> pageApply(Page<AdoptApply> page, Integer status, String keyword) {
        LambdaQueryWrapper<AdoptApply> wrapper = Wrappers.<AdoptApply>lambdaQuery();
        if (status != null) {
            wrapper.eq(AdoptApply::getStatus, status);
        }
        wrapper.orderByDesc(AdoptApply::getCreateTime).orderByDesc(AdoptApply::getId);
        Page<AdoptApply> result = this.page(page, wrapper);

        // 填充宠物信息与申请人信息
        List<AdoptApply> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> petIds = records.stream().map(AdoptApply::getPetId).filter(Objects::nonNull).distinct().toList();
            List<Long> userIds = records.stream().map(AdoptApply::getUserId).filter(Objects::nonNull).distinct().toList();
            Map<Long, Pet> petMap = petIds.isEmpty() ? Map.of()
                    : petMapper.selectList(Wrappers.<Pet>lambdaQuery().in(Pet::getId, petIds)).stream()
                    .collect(Collectors.toMap(Pet::getId, Function.identity(), (a, b) -> a));
            Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                    : sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, userIds)).stream()
                    .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
            for (AdoptApply apply : records) {
                Pet pet = petMap.get(apply.getPetId());
                if (pet != null) {
                    apply.setPetName(pet.getName());
                    apply.setPetCover(pet.getCoverImg());
                }
                SysUser user = userMap.get(apply.getUserId());
                if (user != null) {
                    apply.setUserNickname(user.getNickname());
                    apply.setUsername(user.getUsername());
                }
            }
        }
        return result;
    }

    /**
     * 查询某用户的申请列表
     *
     * @param userId 用户ID
     * @return 申请列表
     */
    @Override
    public List<AdoptApply> listByUser(Long userId) {
        List<AdoptApply> list = this.list(Wrappers.<AdoptApply>lambdaQuery()
                .eq(AdoptApply::getUserId, userId)
                .orderByDesc(AdoptApply::getCreateTime));
        if (list.isEmpty()) {
            return list;
        }
        // 批量查询宠物信息，填充宠物名称与封面图
        List<Long> petIds = list.stream().map(AdoptApply::getPetId).filter(Objects::nonNull).distinct().toList();
        Map<Long, Pet> petMap = petIds.isEmpty() ? Map.of()
                : petMapper.selectList(Wrappers.<Pet>lambdaQuery().in(Pet::getId, petIds)).stream()
                .collect(Collectors.toMap(Pet::getId, Function.identity(), (a, b) -> a));
        // 批量查询申请人信息，填充昵称与账号（小程序端"我的申请"页会展示）
        List<Long> userIds = list.stream().map(AdoptApply::getUserId).filter(Objects::nonNull).distinct().toList();
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                : sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, userIds)).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
        for (AdoptApply apply : list) {
            Pet pet = petMap.get(apply.getPetId());
            if (pet != null) {
                apply.setPetName(pet.getName());
                apply.setPetCover(pet.getCoverImg());
            }
            SysUser user = userMap.get(apply.getUserId());
            if (user != null) {
                apply.setUserNickname(user.getNickname());
                apply.setUsername(user.getUsername());
            }
        }
        return list;
    }

    /**
     * 提交领养申请
     *
     * @param apply 申请信息
     * @return 申请ID
     */
    @Override
    public Long submitApply(AdoptApply apply) {
        // 校验宠物是否存在
        Pet pet = petMapper.selectById(apply.getPetId());
        if (pet == null) {
            throw new BusinessException("该宠物信息不存在");
        }
        if (pet.getStatus() != null && pet.getStatus() == 2) {
            throw new BusinessException("该宠物已被领养，请选择其他宠物");
        }
        // 校验是否重复申请
        Long count = this.count(Wrappers.<AdoptApply>lambdaQuery()
                .eq(AdoptApply::getPetId, apply.getPetId())
                .eq(AdoptApply::getUserId, apply.getUserId())
                .in(AdoptApply::getStatus, List.of(0, 1)));
        if (count != null && count > 0) {
            throw new BusinessException("您已提交过该宠物的领养申请，请勿重复提交");
        }
        apply.setId(null);
        apply.setStatus(0);
        apply.setCreateTime(LocalDateTime.now());
        apply.setUpdateTime(LocalDateTime.now());
        this.save(apply);
        return apply.getId();
    }

    /**
     * 审核领养申请
     *
     * @param id          申请ID
     * @param status      审核结果：1 通过 2 驳回
     * @param auditRemark 审核备注
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(Long id, Integer status, String auditRemark) {
        AdoptApply apply = this.getById(id);
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        // 已审核过的申请不允许重复审核
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            throw new BusinessException("该申请已审核，无法重复操作");
        }
        apply.setStatus(status);
        apply.setAuditRemark(auditRemark);
        apply.setUpdateTime(LocalDateTime.now());
        boolean ok = this.updateById(apply);
        // 审核通过后将宠物状态置为已领养
        if (ok && status != null && status == 1) {
            Pet pet = new Pet();
            pet.setId(apply.getPetId());
            pet.setStatus(2);
            pet.setUpdateTime(LocalDateTime.now());
            petMapper.updateById(pet);
        }
        return ok;
    }

    /**
     * 取消领养申请
     *
     * @param id     申请ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    @Override
    public boolean cancel(Long id, Long userId) {
        AdoptApply apply = this.getById(id);
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        // 只能取消自己的申请
        if (!apply.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的申请");
        }
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            throw new BusinessException("该申请已审核，无法取消");
        }
        apply.setStatus(3);
        apply.setAuditRemark("用户主动取消");
        apply.setUpdateTime(LocalDateTime.now());
        return this.updateById(apply);
    }
}
