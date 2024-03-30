package com.prazk.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/short-link")
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService;
    /**
     * 调用中台分页查询接口
     */
    @GetMapping("/page")
    public Result<IPage<LinkPageVO>> page(LinkPageDTO linkPageDTO) {
        return shortLinkRemoteService.page(linkPageDTO);
    }

    /**
     * 调用中台创建短链接接口
     */
    @PostMapping
    public Result<LinkAddVO> addLink(@RequestBody LinkAddDTO linkAddDTO) {
        return shortLinkRemoteService.addLink(linkAddDTO);
    }
}
