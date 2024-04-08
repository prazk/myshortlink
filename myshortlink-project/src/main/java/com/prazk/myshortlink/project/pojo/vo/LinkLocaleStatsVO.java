package com.prazk.myshortlink.project.pojo.vo;

import lombok.Data;

@Data
public class LinkLocaleStatsVO {
    private Integer cnt;
    private String locale;
    private Double ratio;
}
