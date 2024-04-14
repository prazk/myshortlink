package com.prazk.myshortlink.project.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.biz.mapper.LinkAccessLogsMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkAccessLogs;
import com.prazk.myshortlink.project.biz.service.LinkAccessLogsService;
import org.springframework.stereotype.Service;

@Service
public class LinkAccessLogsServiceImpl extends ServiceImpl<LinkAccessLogsMapper, LinkAccessLogs> implements LinkAccessLogsService {

}

