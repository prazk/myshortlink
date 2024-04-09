package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkOsStats;
import com.prazk.myshortlink.project.pojo.vo.LinkOsStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStats> {

    void recordOsAccessStats(@Param("et") LinkOsStats osStats);

    List<LinkOsStatsVO> selectOsStats(@Param("shortUri") String shortUri);
}

