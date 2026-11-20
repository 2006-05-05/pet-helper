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
 * 留言评论实体类，对应数据库表 comment
 *
 * @author Java1234
 */
@Data
@TableName("comment")
public class Comment implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宠物ID */
    private Long petId;

    /** 留言用户ID */
    private Long userId;

    /** 留言内容 */
    private String content;

    /** 状态：1 显示 0 隐藏 */
    private Integer status;

    /** 留言时间 */
    private LocalDateTime createTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    /** 留言人昵称（非数据库字段） */
    @TableField(exist = false)
    private String nickname;

    /** 留言人头像（非数据库字段） */
    @TableField(exist = false)
    private String avatar;
}
