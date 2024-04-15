package com.prazk.myshortlink.project.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短链接查询 VO
 */
@Data
public class RecyclePageVO {
    //完整短链接
    private String fullShortUrl;
    //原始链接
    private String originUrl;
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
    private String describe;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
