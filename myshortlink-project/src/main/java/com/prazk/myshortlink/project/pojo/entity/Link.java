package com.prazk.myshortlink.project.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Link {
    //ID
    private Long id;
    //域名
    private String domain;
    //短链接
    private String shortUri;
    //完整短链接
    private String fullShortUri;
    //原始链接
    private String originUri;
    //点击量
    private Integer clickNum;
    //分组标识
    private String gid;
    //启用标识 0：未启用 1：已启用
    private Integer enableStatus;
    //创建类型 0：控制台 1：接口
    private Integer createdType;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    private LocalDateTime validDate;
    //描述
    private String describe;
    //创建时间
    private LocalDateTime createTime;
    //修改时间
    private LocalDateTime updateTime;
    //删除标识 0：未删除 1：已删除
    private Integer delFlag;
}
