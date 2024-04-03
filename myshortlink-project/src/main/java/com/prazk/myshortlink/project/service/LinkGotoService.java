package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import com.prazk.myshortlink.project.pojo.entity.LinkGoto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LinkGotoService extends IService<LinkGoto> {

    void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response);
}
