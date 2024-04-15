package com.prazk.myshortlink.project.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkOsStats;
import com.prazk.myshortlink.project.pojo.vo.LinkOsStatsVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStats> {

    void recordOsAccessStats(@Param("et") LinkOsStats osStats);

    List<LinkOsStatsVO> selectOsStats(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("shortUri") String shortUri);
}

