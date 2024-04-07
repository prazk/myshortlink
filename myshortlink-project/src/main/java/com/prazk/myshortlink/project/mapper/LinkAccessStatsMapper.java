package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessDailyStatsVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStats> {
    void recordBasicAccessStats(@Param("et") LinkAccessStats linkAccessStats);

    LinkAccessDailyStatsVO selectStats(@Param("shortUri") String shortUri, @Param("accessDate") LocalDate accessDate);
}
