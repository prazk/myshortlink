package com.prazk.myshortlink.project.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LinkLocaleStatsVO extends LinkInfoStatsAbstractVO {
    private String locale;
}
