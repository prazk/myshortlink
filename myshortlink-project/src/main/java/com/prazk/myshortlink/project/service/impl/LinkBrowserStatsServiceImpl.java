package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkBrowserStatsMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkBrowserStats;
import com.prazk.myshortlink.project.service.LinkBrowserStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkBrowserStatsServiceImpl extends ServiceImpl<LinkBrowserStatsMapper, LinkBrowserStats> implements LinkBrowserStatsService {

}

