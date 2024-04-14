package com.prazk.myshortlink.project.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LinkGotoService extends IService<LinkGoto> {

    void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response);
}
