package com.prazk.myshortlink.project.biz.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LinkUserTypeStatsVO extends LinkInfoStatsAbstractVO {
    private String type;
}
