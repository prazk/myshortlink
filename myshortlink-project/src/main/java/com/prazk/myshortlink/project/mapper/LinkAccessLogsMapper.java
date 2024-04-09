package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessLogs;
import com.prazk.myshortlink.project.pojo.vo.LinkTopIPStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (TLinkAccessLogs)表数据库访问层
 *
 * @author makejava
 * @since 2024-04-07 21:34:50
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogs> {
    void recordAccessLogs(@Param("et") LinkAccessLogs linkAccessLogs);

    List<LinkTopIPStatsVO> selectTopIP(@Param("shortUri") String shortUri);
}

