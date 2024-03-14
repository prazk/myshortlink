package com.prazk.myshortlink.admin.pojo.dto;

import lombok.Data;

/**
 * 用户名密码登录
 */
@Data
public class UserLoginDTO {
    //用户名
    private String username;
    //密码
    private String password;
}
