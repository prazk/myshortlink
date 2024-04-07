package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkLocaleStats;
import org.apache.ibatis.annotations.Param;

public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStats> {
    void recordLocalAccessStats(@Param("et") LinkLocaleStats localeStats);
}
