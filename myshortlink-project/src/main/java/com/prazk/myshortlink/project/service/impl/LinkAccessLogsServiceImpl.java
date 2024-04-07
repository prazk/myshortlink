package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.mapper.LinkAccessLogsMapper;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessLogs;
import com.prazk.myshortlink.project.service.LinkAccessLogsService;
import org.springframework.stereotype.Service;

@Service
public class LinkAccessLogsServiceImpl extends ServiceImpl<LinkAccessLogsMapper, LinkAccessLogs> implements LinkAccessLogsService {

}

