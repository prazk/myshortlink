package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.LinkAccessStatsMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessDailyStatsVO;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessStatsVO;
import com.prazk.myshortlink.project.service.LinkAccessStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStats> implements LinkAccessStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    /**
     * 查询指定日期内总的PV、UV、IP，以及每天的PV、UV、IP
     */
    @Override
    public LinkAccessStatsVO getStats(LinkAccessStatsDTO linkAccessStatsDTO) {
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

        return LinkAccessStatsVO.builder()
                .uip(uip)
                .pv(pv)
                .uv(uv)
                .daily(daily)
                .build();
    }
}
