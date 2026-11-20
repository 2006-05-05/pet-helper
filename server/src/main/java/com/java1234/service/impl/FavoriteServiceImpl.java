package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.java1234.entity.Favorite;
import com.java1234.mapper.FavoriteMapper;
import com.java1234.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏服务实现类
 *
 * @author Java1234
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    /**
     * 收藏宠物
     *
     * @param userId 用户ID
     * @param petId  宠物ID
     * @return 是否收藏成功
     */
    @Override
    public boolean addFavorite(Long userId, Long petId) {
        // 判断是否已收藏
        Long count = this.count(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPetId, petId));
        if (count != null && count > 0) {
            return true;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        favorite.setCreateTime(LocalDateTime.now());
        return this.save(favorite);
    }

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param petId  宠物ID
     * @return 是否取消成功
     */
    @Override
    public boolean removeFavorite(Long userId, Long petId) {
        // 逻辑删除收藏记录
        return this.remove(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPetId, petId));
    }

    /**
     * 查询用户收藏的宠物ID列表
     *
     * @param userId 用户ID
     * @return 宠物ID列表
     */
    @Override
    public List<Long> listPetIds(Long userId) {
        return this.list(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getUserId, userId))
                .stream().map(Favorite::getPetId).toList();
    }
}
