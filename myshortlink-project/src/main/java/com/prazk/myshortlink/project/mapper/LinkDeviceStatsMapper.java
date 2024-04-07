package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkDeviceStats;
import org.apache.ibatis.annotations.Param;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStats> {

    void recordDeviceAccessStats(@Param("et") LinkDeviceStats linkDeviceStats);

}

