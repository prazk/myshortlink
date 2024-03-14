package com.prazk.myshortlink.admin.pojo.dto;

import lombok.Data;

@Data
public class UserLogoutDTO {
    // 用户名
    private String username;
    // token
    private String token;
}
