package com.java1234.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.java1234.entity.Pet;
import com.java1234.entity.PetImage;

import java.util.List;

/**
 * 宠物信息服务接口
 *
 * @author Java1234
 */
public interface PetService extends IService<Pet> {

    /**
     * 分页查询宠物列表（支持关键字、分类、城市、性别、状态筛选）
     *
     * @param page     分页对象
     * @param keyword  关键字（昵称或描述）
     * @param categoryId 分类ID
     * @param city     城市
     * @param gender   性别
     * @param status   状态
     * @param userId   发布人ID
     * @return 分页结果
     */
    Page<Pet> pagePet(Page<Pet> page, String keyword, Long categoryId, String city,
                      String gender, Integer status, Long userId);

    /**
     * 查询宠物详情（含相册图片）
     *
     * @param id     宠物ID
     * @param userId 当前登录用户ID，用于标记收藏状态，可为 null
     * @return 宠物详情
     */
    Pet getDetail(Long id, Long userId);

    /**
     * 批量查询宠物信息并填充扩展字段（分类名称、发布人昵称）
     *
     * <p>用于收藏列表等场景：直接按主键查询不会自动填充扩展字段，
     * 需要显式调用本方法才能拿到 categoryName、userNickname。</p>
     *
     * @param petIds 宠物ID集合
     * @return 宠物列表
     */
    List<Pet> listByIdsWithExtra(List<Long> petIds);

    /**
     * 查询宠物相册
     *
     * @param petId 宠物ID
     * @return 相册列表
     */
    List<PetImage> listImages(Long petId);

    /**
     * 保存宠物及其相册
     *
     * @param pet        宠物信息
     * @param imageUrls  相册图片地址集合
     * @return 宠物ID
     */
    Long savePet(Pet pet, List<String> imageUrls);

    /**
     * 修改宠物信息及其相册
     *
     * @param pet        宠物信息
     * @param imageUrls  相册图片地址集合
     * @return 是否成功
     */
    boolean updatePet(Pet pet, List<String> imageUrls);

    /**
     * 修改宠物上下架状态
     *
     * @param id     宠物ID
     * @param status 目标状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}
