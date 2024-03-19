package com.prazk.myshortlink.admin.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginVO {
    private String token;
    private String username;
}
