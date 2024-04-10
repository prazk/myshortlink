package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkStatsTodayMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkStatsToday;
import com.prazk.myshortlink.project.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsToday> implements LinkStatsTodayService {

}

