package com.prazk.myshortlink.project.biz.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkStatsLogsVO {
    //短链接
    private String shortUri;
    //访客类型
    private String uvType;
    //用户
    private String user;
    //浏览器
    private String browser;
    //操作系统
    private String os;
    //访问设备类型
    private Integer device;
    //IP
    private String ip;
    //省份
    private String province;
    //市名称
    private String city;
    //访问时间
    private LocalDateTime createTime;
}
