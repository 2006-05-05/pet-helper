package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领养申请实体类，对应数据库表 adopt_apply
 *
 * @author Java1234
 */
@Data
@TableName("adopt_apply")
public class AdoptApply implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请的宠物ID */
    private Long petId;

    /** 申请人ID */
    private Long userId;

    /** 领养理由 */
    private String reason;

    /** 居住地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 养宠经验 */
    private String experience;

    /** 状态：0 待审核 1 已通过 2 已驳回 3 已取消 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 申请时间 */
    private LocalDateTime createTime;

    /** 审核时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    /** 宠物名称（非数据库字段） */
    @TableField(exist = false)
    private String petName;

    /** 宠物封面（非数据库字段） */
    @TableField(exist = false)
    private String petCover;

    /** 申请人昵称（非数据库字段） */
    @TableField(exist = false)
    private String userNickname;

    /** 申请人账号（非数据库字段） */
    @TableField(exist = false)
    private String username;
}
