package com.java1234.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 宠物信息保存请求参数
 *
 * @author Java1234
 */
@Data
public class PetDTO implements Serializable {

    /** 宠物ID，新增时为空 */
    private Long id;

    /** 宠物昵称 */
    private String name;

    /** 分类ID */
    private Long categoryId;

    /** 年龄 */
    private String age;

    /** 性别 */
    private String gender;

    /** 所在城市 */
    private String city;

    /** 领养费用 */
    private java.math.BigDecimal price;

    /** 健康状况 */
    private String health;

    /** 详细介绍 */
    private String description;

    /** 封面图片 */
    private String coverImg;

    /** 是否已接种疫苗 */
    private Integer vaccine;

    /** 是否已绝育 */
    private Integer sterilized;

    /** 是否已驱虫 */
    private Integer deworm;

    /** 状态 */
    private Integer status;

    /** 相册图片地址集合 */
    private List<String> images;
}
