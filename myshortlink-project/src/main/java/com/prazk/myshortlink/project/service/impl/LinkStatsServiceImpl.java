package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.*;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.query.LinkDailyDistributionQuery;
import com.prazk.myshortlink.project.pojo.query.LinkWeekdayStatsQuery;
import com.prazk.myshortlink.project.pojo.vo.*;
import com.prazk.myshortlink.project.service.LinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LinkStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStats> implements LinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;

    /**
     * 查询单个短链接的所有统计数据
     */
    @Override
    public LinkStatsVO getStats(LinkAccessStatsDTO linkAccessStatsDTO) {
        // 查询指定日期内总的PV、UV、IP，以及每天的PV、UV、IP
        String shortUri = linkAccessStatsDTO.getShortUri();
        LocalDate startDate = linkAccessStatsDTO.getStartDate();
        LocalDate endDate = linkAccessStatsDTO.getEndDate();
        if (startDate.isAfter(endDate)) {
            throw new ClientException("输入日期有误");
        }

        int uip = 0, uv = 0, pv = 0;
        List<LinkAccessDailyStatsVO> daily = new ArrayList<>();
        for (LocalDate accessDate = startDate; !accessDate.equals(endDate.plusDays(1)); accessDate = accessDate.plusDays(1)) {
            // 查询accessDate的PV、UV、IP
            LinkAccessDailyStatsVO dailyStatsVO = linkAccessStatsMapper.selectStats(shortUri, accessDate);
            if (dailyStatsVO != null) {
                daily.add(dailyStatsVO);
                uip += dailyStatsVO.getUip();
                uv += dailyStatsVO.getUv();
                pv += dailyStatsVO.getPv();
            } else {
                dailyStatsVO = new LinkAccessDailyStatsVO(0, 0, 0, accessDate);
                daily.add(dailyStatsVO);
            }
        }

        // 查询当前小时和前23小时内，该短链接的访问量分布
        Integer nowHour = LocalDateTime.now().getHour();
        LocalDate today = LocalDate.now();
        List<Integer> distribution = new ArrayList<>(24);
        Map<Integer, LinkDailyDistributionQuery> queries = linkAccessStatsMapper.selectDailyDistribution(nowHour, today.minusDays(1), today, shortUri);
        for (int i = nowHour + 1; i < nowHour + 1 + 24; i++) {
            int h = i % 24;
            distribution.add(queries.containsKey(h) ? queries.get(h).getCnt() : 0);
        }

        // 查询指定时间范围内高频访问IP TOP10
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        List<LinkTopIPStatsVO> topIpStats = linkAccessLogsMapper.selectTopIP(startDateTime, endDateTime, shortUri);

        // 一周分布：查询本周时间范围内的指定短链接每天的访问PV数
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Map<Integer, LinkWeekdayStatsQuery> weekQueries = linkAccessStatsMapper.selectWeekStats(startOfWeek, endOfWeek, shortUri);
        List<Integer> weekdayStats = new ArrayList<>(7);
        for (int i = 1; i < 8; i++) {
            weekdayStats.add(weekQueries.containsKey(i) ? weekQueries.get(i).getCnt(): 0);
        }

        // 查询操作系统、设备类型、浏览器统计，是指定时间范围内的统计
        List<LinkBrowserStatsVO> browserStats = linkBrowserStatsMapper.selectBrowserStats(startDate, endDate, shortUri);
        List<LinkOsStatsVO> osStats = linkOsStatsMapper.selectOsStats(startDate, endDate, shortUri);
        List<LinkDeviceStatsVO> deviceStats = linkDeviceStatsMapper.selectDeviceStats(startDate, endDate, shortUri);
        calRatio(browserStats);
        calRatio(osStats);
        calRatio(deviceStats);

        // 查询地区统计数据：查询出指定时间范围内总访问量最高的前 10 个省份，占比以及次数，并按降序排序
        List<LinkLocaleStatsVO> localeStats = linkLocaleStatsMapper.selectLocaleStats(startDate, endDate, shortUri);
        calRatio(localeStats);

        // 统计访客类型：新访客 or 老访客：新访客指首次访问该短链接的用户，此后访问的都是老访客
        // 在数据展示界面中，会展示指定时间范围内该短链接的新访客数与老访客数


        return LinkStatsVO.builder()
                .uip(uip)
                .pv(pv)
                .uv(uv)
                .daily(daily)
                .localeStats(localeStats)
                .distribution(distribution)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .deviceStats(deviceStats)
                .osStats(osStats)
                .build();
    }

    private <T extends LinkInfoStatsAbstractVO> void calRatio(List<T> t) {
        int total = t.stream().mapToInt(T::getCnt).sum();
        DecimalFormat df = new DecimalFormat("#.##");
        t.forEach(vo -> vo.setRatio(Double.parseDouble(df.format((double) vo.getCnt() / total))));
    }
}
