package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告资讯实体类，对应数据库表 notice
 *
 * @author Java1234
 */
@Data
@TableName("notice")
public class Notice implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公告标题 */
    private String title;

    /** 公告正文 */
    private String content;

    /** 封面图片 */
    private String cover;

    /** 类型：通知/活动/攻略 */
    private String type;

    /** 阅读量 */
    private Integer viewCount;

    /** 状态：1 展示 0 隐藏 */
    private Integer status;

    /** 发布时间 */
    private LocalDateTime createTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;
}
