package com.prazk.myshortlink.project.controller;

import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/short-link/link/v1/link")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    /**
     * 创建单短链接
     */
    @PostMapping
    public Result<LinkAddVO> addLink(@RequestBody LinkAddDTO linkAddDTO) {
        LinkAddVO linkAddVO = linkService.addLink(linkAddDTO);
        return Results.success(linkAddVO);
    }
}
