package com.prazk.myshortlink.project.biz.pojo.query;

import lombok.Data;

@Data
public class LinkTodayLogsQuery {
    private String shortUri;
    //今日PV
    private Integer todayPv;
    //今日UV
    private Integer todayUv;
    //今日IP数
    private Integer todayUip;
}
