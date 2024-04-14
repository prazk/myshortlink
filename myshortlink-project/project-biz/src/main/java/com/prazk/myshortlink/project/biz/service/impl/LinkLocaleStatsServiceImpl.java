package com.prazk.myshortlink.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkLocaleStatsMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkLocaleStats;
import com.prazk.myshortlink.project.biz.service.LinkLocaleStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStats> implements LinkLocaleStatsService {
}
