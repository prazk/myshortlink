package com.prazk.myshortlink.project.biz.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.biz.pojo.entity.Link;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.biz.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkUpdateDTO;

import java.util.List;

public interface LinkService extends IService<Link> {

    LinkAddVO addLink(LinkAddDTO linkAddDTO);
    Page<LinkPageVO> page(LinkPageDTO linkPageDTO);
    List<LinkCountVO> listLinkCount(LinkCountDTO linkCountDTO);
    void updateLink(LinkUpdateDTO linkUpdateDTO);
}
