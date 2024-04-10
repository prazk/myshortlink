package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkStatsToday;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * (TLinkStatsToday)表数据库访问层
 *
 * @author makejava
 * @since 2024-04-10 15:27:12
 */
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsToday> {

    void recordTodayLogs(@Param("et") LinkStatsToday linkStatsToday,
                         @Param("uvIncrement") Long uvIncrement,
                         @Param("ipIncrement") Long ipIncrement);

    @MapKey("shortUri")
    Map<String, Object> selectTodayLogs(@Param("shortUris") Set<String> shortUris,
                                                    @Param("today") LocalDate today);
}

