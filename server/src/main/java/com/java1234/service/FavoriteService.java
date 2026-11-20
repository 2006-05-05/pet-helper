package com.java1234.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.Favorite;

import java.util.List;

/**
 * 收藏服务接口
 *
 * @author Java1234
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 收藏宠物
     *
     * @param userId 用户ID
     * @param petId  宠物ID
     * @return 是否收藏成功
     */
    boolean addFavorite(Long userId, Long petId);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param petId  宠物ID
     * @return 是否取消成功
     */
    boolean removeFavorite(Long userId, Long petId);

    /**
     * 查询用户收藏的宠物ID列表
     *
     * @param userId 用户ID
     * @return 宠物ID列表
     */
    List<Long> listPetIds(Long userId);
}
