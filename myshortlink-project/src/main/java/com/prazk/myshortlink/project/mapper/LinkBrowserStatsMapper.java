package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkBrowserStats;
import org.apache.ibatis.annotations.Param;

public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStats> {

    void recordBrowserAccessStats(@Param("et") LinkBrowserStats linkBrowserStats);

}

