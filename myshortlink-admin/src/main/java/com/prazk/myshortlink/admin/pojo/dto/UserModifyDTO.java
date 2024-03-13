package com.prazk.myshortlink.admin.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyDTO {
    //用户名
    private String username;
    //密码
    private String password;
    //手机号
    private String phone;
    //邮箱
    private String mail;
}