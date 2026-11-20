package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物信息实体类，对应数据库表 pet
 *
 * @author Java1234
 */
@Data
@TableName("pet")
public class Pet implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宠物昵称 */
    private String name;

    /** 所属分类ID */
    private Long categoryId;

    /** 年龄描述 */
    private String age;

    /** 性别：弟弟/妹妹/未知 */
    private String gender;

    /** 所在城市 */
    private String city;

    /** 领养费用 */
    private BigDecimal price;

    /** 健康状况 */
    private String health;

    /** 详细介绍 */
    private String description;

    /** 封面图片 */
    private String coverImg;

    /** 是否已接种疫苗：1 是 0 否 */
    private Integer vaccine;

    /** 是否已绝育：1 是 0 否 */
    private Integer sterilized;

    /** 是否已驱虫：1 是 0 否 */
    private Integer deworm;

    /** 状态：1 待领养 2 已领养 0 下架 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 发布人ID */
    private Long userId;

    /** 发布时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    /** 所属分类名称（非数据库字段，用于列表展示） */
    @TableField(exist = false)
    private String categoryName;

    /** 发布人昵称（非数据库字段，用于列表展示） */
    @TableField(exist = false)
    private String userNickname;

    /** 是否已收藏（非数据库字段，0 否 1 是） */
    @TableField(exist = false)
    private Integer favoriteFlag;

    /** 宠物相册（非数据库字段，仅详情返回时填充） */
    @TableField(exist = false)
    private java.util.List<PetImage> images;
}
