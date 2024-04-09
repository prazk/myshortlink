package com.prazk.myshortlink.project.controller;

import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsVO;
import com.prazk.myshortlink.project.service.LinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/short-link/project/stats"))
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
}