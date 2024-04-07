package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkDeviceStatsMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkDeviceStats;
import com.prazk.myshortlink.project.service.LinkDeviceStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkDeviceStatsServiceImpl extends ServiceImpl<LinkDeviceStatsMapper, LinkDeviceStats> implements LinkDeviceStatsService {

}

