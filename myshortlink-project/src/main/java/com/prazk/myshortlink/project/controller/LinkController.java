package com.prazk.myshortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.project.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 短链接分页查询
     */
    @GetMapping("/page")
    public Result<IPage<LinkPageVO>> page(LinkPageDTO linkPageDTO) {
        IPage<LinkPageVO> result = linkService.page(linkPageDTO);
        return Results.success(result);
    }
}
