package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.LinkAccessStatsMapper;
import com.prazk.myshortlink.project.mapper.LinkLocaleStatsMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.query.LinkDailyDistributionQuery;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessDailyStatsVO;
import com.prazk.myshortlink.project.pojo.vo.LinkLocaleStatsVO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsVO;
import com.prazk.myshortlink.project.service.LinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LinkStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStats> implements LinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

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
            // select sum(pv),sum(uv),sum(uip),access_date from t_link_access_stats where short_uri = '3nlRrO' and access_date = '2024-04-07';
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

        // 查询地区统计数据
        // 查询出指定时间范围内总访问量最高的前 10 个省份，占比以及次数，并按降序排序
        List<LinkLocaleStatsVO> localeStats = linkLocaleStatsMapper.selectLocaleStats(startDate, endDate, shortUri);
        int total = localeStats.stream().mapToInt(LinkLocaleStatsVO::getCnt).sum();
        DecimalFormat df = new DecimalFormat("#.##");
        localeStats.forEach(vo -> vo.setRatio(Double.parseDouble(df.format((double) vo.getCnt() / total))));

        // 查询当前小时和前23小时内，该短链接的访问量分布
        Integer nowHour = LocalDateTime.now().getHour();
        LocalDate today = LocalDate.now();
        List<Integer> distribution = new ArrayList<>(24);
        Map<Integer, LinkDailyDistributionQuery> queries = linkAccessStatsMapper.selectDailyDistribution(nowHour, today.minusDays(1), today, shortUri);
        for (int i = nowHour + 1; i < nowHour + 1 + 24; i++) {
            int h = i % 24;
            distribution.add(queries.containsKey(h) ? queries.get(h).getCnt() : 0);
        }

        LinkStatsVO vo = LinkStatsVO.builder()
                .uip(uip)
                .pv(pv)
                .uv(uv)
                .daily(daily)
                .localeStats(localeStats)
                .distribution(distribution)
                .build();

        return vo;
    }
}