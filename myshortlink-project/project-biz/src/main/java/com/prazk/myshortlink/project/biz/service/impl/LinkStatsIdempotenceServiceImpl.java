package com.prazk.myshortlink.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkStatsIdempotenceMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkStatsIdempotence;
import com.prazk.myshortlink.project.biz.service.LinkStatsIdempotenceService;
import org.springframework.stereotype.Service;

@Service
public class LinkStatsIdempotenceServiceImpl extends ServiceImpl<LinkStatsIdempotenceMapper, LinkStatsIdempotence> implements LinkStatsIdempotenceService {

}

