package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物分类实体类，对应数据库表 pet_category
 *
 * @author Java1234
 */
@Data
@TableName("pet_category")
public class PetCategory implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类图标（emoji） */
    private String icon;

    /** 分类描述 */
    private String description;

    /** 显示排序 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;
}
