package com.prazk.myshortlink.project.controller;

import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import com.prazk.myshortlink.project.service.LinkGotoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkGotoController {

    private final LinkGotoService linkGotoService;

    /**
     * 短链接跳转到长链接，发送302重定向
     * 用户发送短链接请求，如 https://my-link.cn/1sMmoK
     */
    @GetMapping("/{shortUri}")
    public void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response) {
        linkGotoService.restore(linkRestoreDTO, request, response);
    }
}
