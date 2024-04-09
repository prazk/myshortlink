package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkBrowserStats;
import com.prazk.myshortlink.project.pojo.vo.LinkBrowserStatsVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStats> {

    void recordBrowserAccessStats(@Param("et") LinkBrowserStats linkBrowserStats);

    List<LinkBrowserStatsVO> selectBrowserStats(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("shortUri") String shortUri);
}

