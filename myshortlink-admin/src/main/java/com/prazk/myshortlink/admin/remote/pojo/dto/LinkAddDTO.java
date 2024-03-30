package com.prazk.myshortlink.admin.remote.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkAddDTO {
    //域名
    private String domain;
    //原始链接
    private String originUri;
    //分组标识
    private String gid;
    //创建类型 0：控制台 1：接口
    private Integer createdType;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    private LocalDateTime validDate;
    //描述
    private String description;
}
