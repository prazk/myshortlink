package com.prazk.myshortlink.project.biz.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.biz.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.dto.*;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;

import java.util.List;

public interface LinkService extends IService<Link> {

    LinkAddVO addLink(LinkAddDTO linkAddDTO);
    Page<LinkPageVO> page(LinkPageDTO linkPageDTO);
    List<LinkCountVO> listLinkCount(LinkCountDTO linkCountDTO);
    Void updateLink(LinkUpdateDTO linkUpdateDTO);
    String getTitleByLink(LinkTitleDTO linkTitleDTO);

}
