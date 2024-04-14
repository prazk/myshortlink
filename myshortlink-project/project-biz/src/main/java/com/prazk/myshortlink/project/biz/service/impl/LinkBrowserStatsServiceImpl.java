package com.prazk.myshortlink.project.biz.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkBrowserStatsMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkBrowserStats;
import com.prazk.myshortlink.project.biz.service.LinkBrowserStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkBrowserStatsServiceImpl extends ServiceImpl<LinkBrowserStatsMapper, LinkBrowserStats> implements LinkBrowserStatsService {

}

