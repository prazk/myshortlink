package com.prazk.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkUpdateDTO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/link")
public class LinkController {

    private final ShortLinkRemoteService shortLinkRemoteService;
    /**
     * 调用中台分页查询接口
     */
    @GetMapping("/page")
    public Result<IPage<LinkPageVO>> page(LinkPageDTO linkPageDTO) {
        return shortLinkRemoteService.pageLink(linkPageDTO);
    }

    /**
     * 调用中台创建短链接接口
     */
    @PostMapping
    public Result<LinkAddVO> addLink(@RequestBody LinkAddDTO linkAddDTO) {
        return shortLinkRemoteService.addLink(linkAddDTO);
    }

    /**
     * 调用中台查询用户的所有分组的短链接数量接口
     */
    @GetMapping("/count")
    public Result<List<LinkCountVO>> listLinkCount(LinkCountDTO linkCountDTO) {
        return shortLinkRemoteService.listLinkCount(linkCountDTO);
    }

    /**
     * 调用中台修改短链接功能
     */
    @PutMapping
    public Result<Void> updateLink(@RequestBody LinkUpdateDTO linkUpdateDTO) {
        return shortLinkRemoteService.updateLink(linkUpdateDTO);
    }
}
