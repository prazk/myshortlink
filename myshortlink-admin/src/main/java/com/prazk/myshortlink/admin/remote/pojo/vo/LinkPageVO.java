package com.prazk.myshortlink.admin.remote.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短链接查询 VO
 */
@Data
public class LinkPageVO {
    //完整短链接
    private String fullShortUri;
    //原始链接
    private String originUri;
    //点击量
    private Integer clickNum;
    //分组标识
    private String gid;
    //启用标识 0：未启用 1：已启用
    private Integer createdType;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    private LocalDateTime validDate;
    //描述
    private String description;
    //创建时间
    private LocalDateTime createTime;
    //修改时间
    private LocalDateTime updateTime;
}
