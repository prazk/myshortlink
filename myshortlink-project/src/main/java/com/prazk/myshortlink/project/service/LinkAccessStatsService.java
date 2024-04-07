package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.vo.LinkAccessStatsVO;

public interface LinkAccessStatsService extends IService<LinkAccessStats> {
    LinkAccessStatsVO getStats(LinkAccessStatsDTO linkAccessStatsDTO);
}
