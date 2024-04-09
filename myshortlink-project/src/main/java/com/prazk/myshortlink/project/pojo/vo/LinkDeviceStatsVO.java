package com.prazk.myshortlink.project.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LinkDeviceStatsVO extends LinkInfoStatsAbstractVO {
    private Integer device;
}
