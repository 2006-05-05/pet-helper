package com.java1234.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 个人资料修改请求参数
 *
 * @author Java1234
 */
@Data
public class ProfileDTO implements Serializable {

    /** 用户昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 联系电话 */
    private String phone;

    /** 性别：男/女/未知 */
    private String gender;

    /** 所在城市 */
    private String city;

    /** 个人简介 */
    private String intro;
}
