package com.java1234.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 领养申请提交参数
 *
 * @author Java1234
 */
@Data
public class ApplyDTO implements Serializable {

    /** 宠物ID */
    private Long petId;

    /** 领养理由 */
    private String reason;

    /** 居住地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 养宠经验 */
    private String experience;
}
