package com.prazk.myshortlink.project.biz.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkDeviceStatsMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkDeviceStats;
import com.prazk.myshortlink.project.biz.service.LinkDeviceStatsService;
import org.springframework.stereotype.Service;

@Service
public class LinkDeviceStatsServiceImpl extends ServiceImpl<LinkDeviceStatsMapper, LinkDeviceStats> implements LinkDeviceStatsService {

}

