package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;

public interface LinkService extends IService<Link> {

    LinkAddVO addLink(LinkAddDTO linkAddDTO);
    IPage<LinkPageVO> page(LinkPageDTO linkPageDTO);
}
