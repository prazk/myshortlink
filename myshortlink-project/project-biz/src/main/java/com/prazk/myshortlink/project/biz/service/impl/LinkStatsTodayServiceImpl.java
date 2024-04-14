package com.prazk.myshortlink.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkStatsTodayMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkStatsToday;
import com.prazk.myshortlink.project.biz.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsToday> implements LinkStatsTodayService {

}

