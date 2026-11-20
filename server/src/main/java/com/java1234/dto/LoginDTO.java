package com.java1234.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数
 *
 * @author Java1234
 */
@Data
public class LoginDTO implements Serializable {

    /** 登录账号 */
    @NotBlank(message = "请输入登录账号")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "请输入登录密码")
    private String password;
}
