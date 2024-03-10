package com.prazk.myshortlink.admin.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    //ID
    private Long id;
    //用户名
    private String username;
    //手机号
    private String phone;
    //邮箱
    private String email;
}
