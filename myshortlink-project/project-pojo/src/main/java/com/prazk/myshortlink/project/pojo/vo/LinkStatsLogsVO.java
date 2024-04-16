package com.prazk.myshortlink.project.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prazk.myshortlink.project.pojo.serializer.DeviceDeserializer;
import com.prazk.myshortlink.project.pojo.serializer.DeviceSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkStatsLogsVO {
    //访客类型
    private String uvType;
    //浏览器
    private String browser;
    //操作系统
    private String os;
    //IP
    private String ip;
    // 网络
    private String network;
    //访问设备类型
    @JsonSerialize(using = DeviceSerializer.class)
    @JsonDeserialize(using = DeviceDeserializer.class)
    private Integer device;
    // 省市
    private String locale;
    //用户
    private String user;
    //访问时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    //省份
    private String province;
    //市名称
    private String city;
}
