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
public class LinkStatsVO {
    private Integer pv;
    private Integer uv;
    private Integer uip;
    /**
     * 每日访问记录
     */
    private List<LinkAccessDailyStatsVO> daily;
    /**
     * 查询时间范围内的访问地区统计
     */
    private List<LinkLocaleStatsVO> localeStats;
    /**
     * 近 24小时内，该短链接的访问量分布
     */
    private List<Integer> distribution;
    /**
     * 查询高频访问IP TOP10
     */
    private List<LinkTopIPStatsVO> topIpStats;
    /**
     * 一周分布：查询本周时间范围内的指定短链接每天的访问PV数
     */
    private List<Integer> weekdayStats;
    /**
     * 统计操作系统
     */
    private List<LinkOsStatsVO> osStats;
    /**
     * 统计浏览器
     */
    private List<LinkBrowserStatsVO> browserStats;
    /**
     * 统计设备类型
     */
    private List<LinkDeviceStatsVO> deviceStats;
}
