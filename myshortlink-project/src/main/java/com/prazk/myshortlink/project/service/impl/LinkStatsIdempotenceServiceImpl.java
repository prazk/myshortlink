package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkStatsIdempotenceMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkStatsIdempotence;
import com.prazk.myshortlink.project.service.LinkStatsIdempotenceService;
import org.springframework.stereotype.Service;

@Service
public class LinkStatsIdempotenceServiceImpl extends ServiceImpl<LinkStatsIdempotenceMapper, LinkStatsIdempotence> implements LinkStatsIdempotenceService {

}

