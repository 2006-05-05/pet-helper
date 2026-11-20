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
 * 用户实体类，对应数据库表 sys_user
 *
 * <p>后台管理员与小程序普通用户共用该表，通过 role 字段区分。</p>
 *
 * @author Java1234
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 登录密码（明文存储，密码统一为 123456） */
    private String password;

    /** 用户昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 联系电话 */
    private String phone;

    /** 性别 */
    private String gender;

    /** 所在城市 */
    private String city;

    /** 个人简介 */
    private String intro;

    /** 角色：admin 管理员 / user 普通用户 */
    private String role;

    /** 状态：1 正常 0 禁用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0 未删除 1 已删除 */
    @TableLogic
    private Integer deleted;
}
