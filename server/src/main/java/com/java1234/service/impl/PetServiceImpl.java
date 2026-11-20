package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.common.BusinessException;
import com.java1234.entity.Favorite;
import com.java1234.entity.Pet;
import com.java1234.entity.PetCategory;
import com.java1234.entity.PetImage;
import com.java1234.entity.SysUser;
import com.java1234.mapper.FavoriteMapper;
import com.java1234.mapper.PetCategoryMapper;
import com.java1234.mapper.PetImageMapper;
import com.java1234.mapper.PetMapper;
import com.java1234.mapper.SysUserMapper;
import com.java1234.service.PetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 宠物信息服务实现类
 *
 * @author Java1234
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    /** 宠物相册数据访问对象 */
    private final PetImageMapper petImageMapper;

    /** 分类数据访问对象 */
    private final PetCategoryMapper petCategoryMapper;

    /** 用户数据访问对象 */
    private final SysUserMapper sysUserMapper;

    /** 收藏数据访问对象 */
    private final FavoriteMapper favoriteMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param petImageMapper   宠物相册数据访问对象
     * @param petCategoryMapper 分类数据访问对象
     * @param sysUserMapper    用户数据访问对象
     * @param favoriteMapper   收藏数据访问对象
     */
    public PetServiceImpl(PetImageMapper petImageMapper, PetCategoryMapper petCategoryMapper,
                           SysUserMapper sysUserMapper, FavoriteMapper favoriteMapper) {
        this.petImageMapper = petImageMapper;
        this.petCategoryMapper = petCategoryMapper;
        this.sysUserMapper = sysUserMapper;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * 分页查询宠物列表
     *
     * @param page       分页对象
     * @param keyword    关键字
     * @param categoryId 分类ID
     * @param city       城市
     * @param gender     性别
     * @param status     状态
     * @param userId     发布人ID
     * @return 分页结果
     */
    @Override
    public Page<Pet> pagePet(Page<Pet> page, String keyword, Long categoryId, String city,
                             String gender, Integer status, Long userId) {
        LambdaQueryWrapper<Pet> wrapper = Wrappers.<Pet>lambdaQuery();
        // 关键字模糊匹配昵称或描述
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Pet::getName, keyword).or().like(Pet::getDescription, keyword));
        }
        // 按分类筛选（空字符串视为不筛选）
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Pet::getCategoryId, categoryId);
        }
        // 按城市筛选
        if (StringUtils.hasText(city)) {
            wrapper.eq(Pet::getCity, city);
        }
        // 按性别筛选
        if (StringUtils.hasText(gender)) {
            wrapper.eq(Pet::getGender, gender);
        }
        // 按状态筛选
        if (status != null) {
            wrapper.eq(Pet::getStatus, status);
        }
        // 按发布人筛选（我的发布）
        if (userId != null) {
            wrapper.eq(Pet::getUserId, userId);
        }
        wrapper.orderByDesc(Pet::getCreateTime).orderByDesc(Pet::getId);
        Page<Pet> result = this.page(page, wrapper);
        this.fillExtra(result.getRecords());
        return result;
    }

    /**
     * 查询宠物详情
     *
     * @param id     宠物ID
     * @param userId 当前登录用户ID
     * @return 宠物详情
     */
    @Override
    public Pet getDetail(Long id, Long userId) {
        Pet pet = this.getById(id);
        if (pet == null) {
            throw new BusinessException("宠物信息不存在或已被删除");
        }
        // 浏览次数加一
        Pet update = new Pet();
        update.setId(id);
        update.setViewCount((pet.getViewCount() == null ? 0 : pet.getViewCount()) + 1);
        this.updateById(update);

        List<Pet> list = new ArrayList<>(List.of(pet));
        this.fillExtra(list);
        Pet detail = list.get(0);
        // 标记当前用户是否已收藏
        if (userId != null) {
            Long count = favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                    .eq(Favorite::getUserId, userId)
                    .eq(Favorite::getPetId, id));
            detail.setFavoriteFlag(count != null && count > 0 ? 1 : 0);
        } else {
            detail.setFavoriteFlag(0);
        }
        return detail;
    }

    /**
     * 批量查询宠物信息并填充扩展字段（分类名称、发布人昵称）
     *
     * @param petIds 宠物ID集合
     * @return 宠物列表
     */
    @Override
    public List<Pet> listByIdsWithExtra(List<Long> petIds) {
        if (petIds == null || petIds.isEmpty()) {
            return List.of();
        }
        List<Pet> pets = this.listByIds(petIds);
        this.fillExtra(pets);
        return pets;
    }

    /**
     * 查询宠物相册
     *
     * @param petId 宠物ID
     * @return 相册列表
     */
    @Override
    public List<PetImage> listImages(Long petId) {
        return petImageMapper.selectList(Wrappers.<PetImage>lambdaQuery()
                .eq(PetImage::getPetId, petId)
                .orderByAsc(PetImage::getSort));
    }

    /**
     * 保存宠物及其相册
     *
     * @param pet       宠物信息
     * @param imageUrls 相册图片地址集合
     * @return 宠物ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long savePet(Pet pet, List<String> imageUrls) {
        pet.setId(null);
        if (pet.getStatus() == null) {
            pet.setStatus(1);
        }
        if (pet.getViewCount() == null) {
            pet.setViewCount(0);
        }
        if (pet.getPrice() == null) {
            pet.setPrice(java.math.BigDecimal.ZERO);
        }
        pet.setCreateTime(LocalDateTime.now());
        pet.setUpdateTime(LocalDateTime.now());
        this.save(pet);
        this.saveImages(pet.getId(), imageUrls);
        return pet.getId();
    }

    /**
     * 修改宠物信息及其相册
     *
     * @param pet       宠物信息
     * @param imageUrls 相册图片地址集合
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePet(Pet pet, List<String> imageUrls) {
        pet.setUpdateTime(LocalDateTime.now());
        // 不允许覆盖浏览次数
        pet.setViewCount(null);
        boolean ok = this.updateById(pet);
        if (imageUrls != null) {
            // 先删除原有相册再重新保存
            petImageMapper.delete(Wrappers.<PetImage>lambdaQuery().eq(PetImage::getPetId, pet.getId()));
            this.saveImages(pet.getId(), imageUrls);
        }
        return ok;
    }

    /**
     * 修改宠物上下架状态
     *
     * @param id     宠物ID
     * @param status 目标状态
     * @return 是否成功
     */
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setStatus(status);
        pet.setUpdateTime(LocalDateTime.now());
        return this.updateById(pet);
    }

    /**
     * 保存宠物相册
     *
     * @param petId     宠物ID
     * @param imageUrls 图片地址集合
     */
    private void saveImages(Long petId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        int sort = 1;
        for (String url : imageUrls) {
            if (url == null || url.isEmpty()) {
                continue;
            }
            PetImage image = new PetImage();
            image.setPetId(petId);
            image.setUrl(url);
            image.setSort(sort++);
            image.setCreateTime(LocalDateTime.now());
            petImageMapper.insert(image);
        }
    }

    /**
     * 填充列表的扩展字段：分类名称、发布人昵称、收藏状态
     *
     * @param pets 宠物列表
     */
    private void fillExtra(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            return;
        }
        // 查询分类名称
        List<Long> categoryIds = pets.stream().map(Pet::getCategoryId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> categoryMap = categoryIds.isEmpty() ? Map.of()
                : petCategoryMapper.selectList(Wrappers.<PetCategory>lambdaQuery().in(PetCategory::getId, categoryIds)).stream()
                .collect(Collectors.toMap(PetCategory::getId, PetCategory::getName, (a, b) -> a));
        // 查询发布人昵称
        List<Long> userIds = pets.stream().map(Pet::getUserId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of()
                : sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, userIds)).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
        for (Pet pet : pets) {
            pet.setCategoryName(categoryMap.get(pet.getCategoryId()));
            SysUser user = userMap.get(pet.getUserId());
            pet.setUserNickname(user == null ? null : user.getNickname());
        }
    }
}
