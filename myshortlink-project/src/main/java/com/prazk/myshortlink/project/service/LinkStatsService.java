package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsVO;

public interface LinkStatsService extends IService<LinkAccessStats> {
    LinkStatsVO getStats(LinkAccessStatsDTO linkAccessStatsDTO);
}
