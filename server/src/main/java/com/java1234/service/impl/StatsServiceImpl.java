package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.java1234.entity.AdoptApply;
import com.java1234.entity.Pet;
import com.java1234.entity.PetCategory;
import com.java1234.entity.SysUser;
import com.java1234.mapper.AdoptApplyMapper;
import com.java1234.mapper.PetCategoryMapper;
import com.java1234.mapper.PetMapper;
import com.java1234.mapper.SysUserMapper;
import com.java1234.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 *
 * <p>基于 MyBatis-Plus 查询并组装管理后台首页所需的统计数据。</p>
 *
 * @author Java1234
 */
@Service
public class StatsServiceImpl implements StatsService {

    /** 用户数据访问对象 */
    private final SysUserMapper sysUserMapper;

    /** 宠物数据访问对象 */
    private final PetMapper petMapper;

    /** 分类数据访问对象 */
    private final PetCategoryMapper petCategoryMapper;

    /** 领养申请数据访问对象 */
    private final AdoptApplyMapper adoptApplyMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param sysUserMapper      用户数据访问对象
     * @param petMapper          宠物数据访问对象
     * @param petCategoryMapper  分类数据访问对象
     * @param adoptApplyMapper   领养申请数据访问对象
     */
    public StatsServiceImpl(SysUserMapper sysUserMapper, PetMapper petMapper,
                             PetCategoryMapper petCategoryMapper, AdoptApplyMapper adoptApplyMapper) {
        this.sysUserMapper = sysUserMapper;
        this.petMapper = petMapper;
        this.petCategoryMapper = petCategoryMapper;
        this.adoptApplyMapper = adoptApplyMapper;
    }

    /**
     * 查询首页概览统计数据
     *
     * @return 统计数据集合
     */
    @Override
    public Map<String, Object> overview() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 用户总数与今日新增
        Long userTotal = sysUserMapper.selectCount(null);
        Long userToday = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .ge(SysUser::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay()));
        data.put("userTotal", userTotal);
        data.put("userToday", userToday);

        // 宠物总数、待领养数量与今日新增
        Long petTotal = petMapper.selectCount(null);
        Long petWait = petMapper.selectCount(Wrappers.<Pet>lambdaQuery().eq(Pet::getStatus, 1));
        Long petAdopted = petMapper.selectCount(Wrappers.<Pet>lambdaQuery().eq(Pet::getStatus, 2));
        Long petToday = petMapper.selectCount(Wrappers.<Pet>lambdaQuery()
                .ge(Pet::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay()));
        data.put("petTotal", petTotal);
        data.put("petWait", petWait);
        data.put("petAdopted", petAdopted);
        data.put("petToday", petToday);

        // 申请总数、待审核数量与今日新增
        Long applyTotal = adoptApplyMapper.selectCount(null);
        Long applyPending = adoptApplyMapper.selectCount(Wrappers.<AdoptApply>lambdaQuery().eq(AdoptApply::getStatus, 0));
        Long applyToday = adoptApplyMapper.selectCount(Wrappers.<AdoptApply>lambdaQuery()
                .ge(AdoptApply::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay()));
        data.put("applyTotal", applyTotal);
        data.put("applyPending", applyPending);
        data.put("applyToday", applyToday);

        // 领养成功率：已通过申请数 / 全部审核过的申请数
        Long applyPassed = adoptApplyMapper.selectCount(Wrappers.<AdoptApply>lambdaQuery().eq(AdoptApply::getStatus, 1));
        Long applyAudited = adoptApplyMapper.selectCount(Wrappers.<AdoptApply>lambdaQuery()
                .in(AdoptApply::getStatus, List.of(1, 2)));
        double rate = applyAudited == null || applyAudited == 0 ? 0d
                : Math.round(applyPassed * 10000d / applyAudited) / 100d;
        data.put("applyPassed", applyPassed);
        data.put("applyRate", rate);
        return data;
    }

    /**
     * 查询宠物分类数量分布
     *
     * @return 分类名称与数量列表
     */
    @Override
    public List<Map<String, Object>> categoryCount() {
        List<PetCategory> categories = petCategoryMapper.selectList(Wrappers.<PetCategory>lambdaQuery()
                .orderByAsc(PetCategory::getSort));
        List<Map<String, Object>> result = new ArrayList<>();
        for (PetCategory category : categories) {
            Long count = petMapper.selectCount(Wrappers.<Pet>lambdaQuery().eq(Pet::getCategoryId, category.getId()));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", category.getName());
            item.put("icon", category.getIcon());
            item.put("value", count);
            result.add(item);
        }
        return result;
    }

    /**
     * 查询近 N 天的领养申请趋势
     *
     * @param days 统计天数
     * @return 日期与数量列表
     */
    @Override
    public List<Map<String, Object>> applyTrend(int days) {
        LocalDate start = LocalDate.now().minusDays(days - 1L);
        List<AdoptApply> applies = adoptApplyMapper.selectList(Wrappers.<AdoptApply>lambdaQuery()
                .ge(AdoptApply::getCreateTime, start.atStartOfDay())
                .orderByAsc(AdoptApply::getCreateTime));
        // 按日期分组统计
        Map<String, Long> grouped = applies.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreateTime().format(DateTimeFormatter.ofPattern("MM-dd")),
                        Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            String label = start.plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd"));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", label);
            item.put("value", grouped.getOrDefault(label, 0L));
            result.add(item);
        }
        return result;
    }

    /**
     * 查询领养申请状态分布
     *
     * @return 状态名称与数量列表
     */
    @Override
    public List<Map<String, Object>> applyStatusCount() {
        String[] names = {"待审核", "已通过", "已驳回", "已取消"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int status = 0; status < names.length; status++) {
            Long count = adoptApplyMapper.selectCount(Wrappers.<AdoptApply>lambdaQuery().eq(AdoptApply::getStatus, status));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", names[status]);
            item.put("value", count);
            result.add(item);
        }
        return result;
    }

    /**
     * 查询各城市的宠物数量分布
     *
     * @return 城市名称与数量列表
     */
    @Override
    public List<Map<String, Object>> cityCount() {
        List<Pet> pets = petMapper.selectList(Wrappers.<Pet>lambdaQuery().select(Pet::getCity));
        Map<String, Long> grouped = pets.stream()
                .filter(p -> p.getCity() != null && !p.getCity().isEmpty())
                .collect(Collectors.groupingBy(Pet::getCity, Collectors.counting()));
        return grouped.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", entry.getKey());
                    item.put("value", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询最新提交的领养申请
     *
     * @param limit 数量限制
     * @return 申请列表
     */
    @Override
    public List<Map<String, Object>> latestApply(int limit) {
        List<AdoptApply> applies = adoptApplyMapper.selectList(Wrappers.<AdoptApply>lambdaQuery()
                .orderByDesc(AdoptApply::getCreateTime)
                .last("limit " + limit));
        List<Map<String, Object>> result = new ArrayList<>();
        for (AdoptApply apply : applies) {
            Pet pet = petMapper.selectById(apply.getPetId());
            SysUser user = sysUserMapper.selectById(apply.getUserId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", apply.getId());
            item.put("petName", pet == null ? "已删除" : pet.getName());
            item.put("petCover", pet == null ? null : pet.getCoverImg());
            item.put("userNickname", user == null ? "匿名用户" : user.getNickname());
            item.put("reason", apply.getReason());
            item.put("status", apply.getStatus());
            item.put("createTime", apply.getCreateTime() == null ? null
                    : apply.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            result.add(item);
        }
        return result;
    }
}
