package com.prazk.myshortlink.project.pojo.dto;

import lombok.Data;

@Data
public class LinkStatsLogsPageDTO {
    private String startDate;
    private String endDate;
    private String shortUri;
    private String fullShortUrl;
    private String gid;
    private Integer current;
    private Integer size;
}
