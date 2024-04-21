package com.prazk.myshortlink.project.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建短链接响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkAddVO {
    //分组标识
    private String gid;
    //原始链接
    private String originUrl;
    //完整短链接
    private String fullShortUrl;
}
