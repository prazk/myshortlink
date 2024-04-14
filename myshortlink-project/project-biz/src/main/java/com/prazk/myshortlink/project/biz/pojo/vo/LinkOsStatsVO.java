package com.prazk.myshortlink.project.biz.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LinkOsStatsVO extends LinkInfoStatsAbstractVO {
    private String os;
}
