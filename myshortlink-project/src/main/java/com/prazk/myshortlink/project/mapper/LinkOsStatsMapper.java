package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkOsStats;
import org.apache.ibatis.annotations.Param;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStats> {

    void recordOsAccessStats(@Param("et") LinkOsStats osStats);
}

