package com.prazk.myshortlink.project.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkAccessLogs;
import com.prazk.myshortlink.project.biz.pojo.query.uvTypeQuery;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkTopIPStatsVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * (TLinkAccessLogs)表数据库访问层
 *
 * @author makejava
 * @since 2024-04-07 21:34:50
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogs> {
    void recordAccessLogs(@Param("et") LinkAccessLogs linkAccessLogs);

    List<LinkTopIPStatsVO> selectTopIP(@Param("startDateTime") LocalDateTime startDateTime,
                                       @Param("endDateTime") LocalDateTime endDateTime,
                                       @Param("shortUri") String shortUri);

    List<String> selectUsers(@Param("startDateTime") LocalDateTime startDateTime,
                             @Param("endDateTime") LocalDateTime endDateTime,
                             @Param("shortUri") String shortUri);

    @MapKey("user")
    Map<String, uvTypeQuery> selectAccessType(@Param("users") List<String> users,
                                              @Param("startDateTime") LocalDateTime startDateTime,
                                              @Param("endDateTime") LocalDateTime endDateTime,
                                              @Param("shortUri") String shortUri);
}

