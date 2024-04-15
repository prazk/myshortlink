package com.prazk.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.api.client.LinkClient;
import com.prazk.myshortlink.project.pojo.dto.LinkAccessStatsDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkStatsLogsPageDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsLogsVO;
import com.prazk.myshortlink.project.pojo.vo.LinkStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("short-link/admin")
public class LinkStatsController {

    private final LinkClient linkClient;

    /**
     * 调用中台查询单个短链接的图表数据
     */
    @GetMapping("/stats")
    public Result<LinkStatsVO> getStats(LinkAccessStatsDTO linkAccessStatsDTO) {
        return linkClient.getStats(BeanUtil.beanToMap(linkAccessStatsDTO));
    }

    /**
     * 调用中台查询单个短链接的访问日志
     */
    @GetMapping("/stats/logs")
    public Result<Page<LinkStatsLogsVO>> getLogs(LinkStatsLogsPageDTO linkStatsLogsPageDTO) {
        return linkClient.getLogs(BeanUtil.beanToMap(linkStatsLogsPageDTO));
    }
}
