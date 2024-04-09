package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.query.LinkDailyDistributionQuery;
import com.prazk.myshortlink.project.pojo.query.LinkWeekdayStatsQuery;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessDailyStatsVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Map;

public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStats> {
    void recordBasicAccessStats(@Param("et") LinkAccessStats linkAccessStats);

    LinkAccessDailyStatsVO selectStats(@Param("shortUri") String shortUri, @Param("accessDate") LocalDate accessDate);

    @MapKey("hour")
    Map<Integer, LinkDailyDistributionQuery> selectDailyDistribution(@Param("nowHour") Integer nowHour,
                                                                     @Param("yesterday") LocalDate yesterday,
                                                                     @Param("today") LocalDate today,
                                                                     @Param("shortUri") String shortUri);

    @MapKey("weekday")
    Map<Integer, LinkWeekdayStatsQuery> selectWeekStats(@Param("startOfWeek") LocalDate startOfWeek,
                                                        @Param("endOfWeek") LocalDate endOfWeek,
                                                        @Param("shortUri") String shortUri);

}
