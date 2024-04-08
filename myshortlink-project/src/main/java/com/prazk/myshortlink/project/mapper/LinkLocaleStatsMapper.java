package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkLocaleStats;
import com.prazk.myshortlink.project.pojo.vo.LinkLocaleStatsVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStats> {
    void recordLocalAccessStats(@Param("et") LinkLocaleStats localeStats);

    List<LinkLocaleStatsVO> selectLocaleStats(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("shortUri") String shortUri);
}
