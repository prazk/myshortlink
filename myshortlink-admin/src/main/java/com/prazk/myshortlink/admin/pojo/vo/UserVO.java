package com.prazk.myshortlink.admin.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prazk.myshortlink.admin.common.serializer.PhoneDesensitizationSerializer;
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
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    //邮箱
    private String email;
}
