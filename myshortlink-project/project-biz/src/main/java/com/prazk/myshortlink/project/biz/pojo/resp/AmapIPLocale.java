package com.prazk.myshortlink.project.biz.pojo.resp;

import lombok.Data;

/**
 * 高德开放平台IP定位服务响应实体
 * {
 * 	"status" : "1",
 * 	"info" : "OK",
 * 	"infocode" : "10000",
 * 	"province" : "北京市",
 * 	"city" : "北京市",
 * 	"adcode" : "110000",
 * 	"rectangle" : "116.0119343,39.66127144;116.7829835,40.2164962"
 * }
 */
@Data
public class AmapIPLocale {
    private String status;
    private String info;
    private String infocode;
    private String province;
    private String city;
    private String adcode;
    private String rectangle;
}
