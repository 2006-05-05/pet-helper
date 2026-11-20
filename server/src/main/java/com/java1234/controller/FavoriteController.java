package com.java1234.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.LoginUser;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.entity.Favorite;
import com.java1234.entity.Pet;
import com.java1234.entity.SysUser;
import com.java1234.service.FavoriteService;
import com.java1234.service.PetService;
import com.java1234.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收藏控制器
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    /** 收藏服务 */
    private final FavoriteService favoriteService;

    /** 宠物服务 */
    private final PetService petService;

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造方法，注入依赖
     *
     * @param favoriteService 收藏服务
     * @param petService      宠物服务
     * @param sysUserService  用户服务
     */
    public FavoriteController(FavoriteService favoriteService, PetService petService, SysUserService sysUserService) {
        this.favoriteService = favoriteService;
        this.petService = petService;
        this.sysUserService = sysUserService;
    }

    /**
     * 收藏宠物
     *
     * @param petId 宠物ID
     * @return 是否成功
     */
    @PostMapping("/{petId}")
    public Result<Void> add(@PathVariable Long petId) {
        favoriteService.addFavorite(currentUserId(), petId);
        return Result.success("收藏成功", null);
    }

    /**
     * 取消收藏
     *
     * @param petId 宠物ID
     * @return 是否成功
     */
    @DeleteMapping("/{petId}")
    public Result<Void> remove(@PathVariable Long petId) {
        favoriteService.removeFavorite(currentUserId(), petId);
        return Result.success("已取消收藏", null);
    }

    /**
     * 查询我的收藏列表
     *
     * @return 收藏的宠物列表
     */
    @GetMapping("/my")
    public Result<List<Pet>> my() {
        Long userId = currentUserId();
        List<Favorite> favorites = favoriteService.list(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));
        // 取出宠物ID并去重，批量查询以填充分类名称与发布人昵称
        List<Long> petIds = favorites.stream()
                .map(Favorite::getPetId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        // 按收藏时间倒序重新排列，保证与收藏顺序一致
        List<Long> orderedIds = favorites.stream()
                .map(Favorite::getPetId)
                .filter(java.util.Objects::nonNull)
                .toList();
        Map<Long, Pet> petMap = petService.listByIdsWithExtra(petIds).stream()
                .collect(Collectors.toMap(Pet::getId, Function.identity(), (a, b) -> a));
        List<Pet> result = orderedIds.stream()
                .distinct()
                .map(petMap::get)
                .filter(java.util.Objects::nonNull)
                .peek(p -> p.setFavoriteFlag(1))
                .toList();
        return Result.success(result);
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    private Long currentUserId() {
        SysUser user = sysUserService.getByUsername(LoginUser.getUsername());
        if (user == null) {
            throw new com.java1234.common.BusinessException("登录状态已失效，请重新登录");
        }
        return user.getId();
    }
}
