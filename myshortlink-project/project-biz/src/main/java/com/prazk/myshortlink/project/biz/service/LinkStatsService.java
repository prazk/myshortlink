package com.prazk.myshortlink.project.biz.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkStatsLogsVO;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkStatsVO;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkStatsLogsPageDTO;

public interface LinkStatsService extends IService<LinkAccessStats> {
    LinkStatsVO getStats(LinkAccessStatsDTO linkAccessStatsDTO);
    Page<LinkStatsLogsVO> getLogs(LinkStatsLogsPageDTO linkStatsLogsPageDTO);
}
