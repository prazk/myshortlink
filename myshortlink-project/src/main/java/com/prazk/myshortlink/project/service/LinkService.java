package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkUpdateDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;

import java.util.List;

public interface LinkService extends IService<Link> {

    LinkAddVO addLink(LinkAddDTO linkAddDTO);
    IPage<LinkPageVO> page(LinkPageDTO linkPageDTO);

    List<LinkCountVO> listLinkCount(LinkCountDTO linkCountDTO);

    void updateLink(LinkUpdateDTO linkUpdateDTO);
}
