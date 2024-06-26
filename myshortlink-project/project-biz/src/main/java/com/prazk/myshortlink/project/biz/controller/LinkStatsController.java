package com.prazk.myshortlink.project.biz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.common.convention.result.Results;
import com.prazk.myshortlink.project.biz.service.LinkStatsService;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkStatsLogsPageDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsLogsVO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("short-link/project/stats"))
@RequiredArgsConstructor
public class LinkStatsController {

    private final LinkStatsService linkStatsService;

    /**
     * 查询指定日期内总的PV、UV、IP，以及每天的PV、UV、IP
     */
    @GetMapping
    public Result<LinkStatsVO> getStats(LinkAccessStatsDTO linkAccessStatsDTO) {
        LinkStatsVO stats = linkStatsService.getStats(linkAccessStatsDTO);
        return Results.success(stats);
    }

    /**
     * 分页查询查询访问日志
     */
    @GetMapping("/logs")
    public Result<Page<LinkStatsLogsVO>> getLogs(LinkStatsLogsPageDTO linkStatsLogsPageDTO) {
        Page<LinkStatsLogsVO> result = linkStatsService.getLogs(linkStatsLogsPageDTO);
        return Results.success(result);
    }
}
