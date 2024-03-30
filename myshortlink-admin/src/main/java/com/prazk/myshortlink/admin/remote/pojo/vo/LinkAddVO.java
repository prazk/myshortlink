package com.prazk.myshortlink.admin.remote.pojo.vo;

import lombok.Data;

/**
 * 创建短链接响应实体
 */
@Data
public class LinkAddVO {
    //分组标识
    private String gid;
    //原始链接
    private String originUri;
    //完整短链接
    private String fullShortUri;
}
