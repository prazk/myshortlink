package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkLocaleStatsMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkLocaleStats;
import com.prazk.myshortlink.project.service.LinkLocaleStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStats> implements LinkLocaleStatsService {
}
