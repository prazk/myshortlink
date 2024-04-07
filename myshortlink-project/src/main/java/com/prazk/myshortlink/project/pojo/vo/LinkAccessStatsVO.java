package com.prazk.myshortlink.project.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkAccessStatsVO {
    private Integer pv;
    private Integer uv;
    private Integer uip;
    private List<LinkAccessDailyStatsVO> daily;
}
