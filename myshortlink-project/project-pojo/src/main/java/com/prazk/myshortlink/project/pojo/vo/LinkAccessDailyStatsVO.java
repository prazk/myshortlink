package com.prazk.myshortlink.project.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 每天的PV、UV、IP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkAccessDailyStatsVO {
    private Integer pv;
    private Integer uv;
    private Integer uip;
    @JsonProperty("date")
    private LocalDate accessDate;
}
