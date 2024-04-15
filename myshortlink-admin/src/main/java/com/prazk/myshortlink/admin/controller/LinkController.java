package com.prazk.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.api.client.LinkClient;
import com.prazk.myshortlink.project.pojo.dto.*;
import com.prazk.myshortlink.project.pojo.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("short-link/admin/link")
public class LinkController {

    private final LinkClient linkClient;

    /**
     * 调用中台分页查询接口
     */
    @GetMapping("/page")
    public Result<Page<LinkPageVO>> page(LinkPageDTO linkPageDTO) {
        return linkClient.pageLink(BeanUtil.beanToMap(linkPageDTO));
    }

    /**
     * 调用中台创建短链接接口
     */
    @PostMapping
    public Result<LinkAddVO> addLink(@RequestBody LinkAddDTO linkAddDTO) {
        return linkClient.addLink(linkAddDTO);
    }

    /**
     * 调用中台查询用户的所有分组的短链接数量接口
     */
    @GetMapping("/count")
    public Result<List<LinkCountVO>> listLinkCount(LinkCountDTO linkCountDTO) {
        return linkClient.listLinkCount(BeanUtil.beanToMap(linkCountDTO));
    }

    /**
     * 调用中台修改短链接功能
     */
    @PutMapping
    public Result<Void> updateLink(@RequestBody LinkUpdateDTO linkUpdateDTO) {
        return linkClient.updateLink(linkUpdateDTO);
    }
}
