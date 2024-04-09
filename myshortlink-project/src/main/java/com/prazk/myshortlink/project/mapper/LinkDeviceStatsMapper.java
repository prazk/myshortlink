package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkDeviceStats;
import com.prazk.myshortlink.project.pojo.vo.LinkDeviceStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStats> {

    void recordDeviceAccessStats(@Param("et") LinkDeviceStats linkDeviceStats);

    List<LinkDeviceStatsVO> selectDeviceStats(@Param("shortUri") String shortUri);
}

