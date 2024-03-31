package com.prazk.myshortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkUpdateDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.project.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/link/v1/link")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    /**
     * 创建短链接
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

    /**
     * 查询用户的所有分组的短链接数量
     */
    @GetMapping("/count")
    public Result<List<LinkCountVO>> listLinkCount(LinkCountDTO linkCountDTO) {
        List<LinkCountVO> result = linkService.listLinkCount(linkCountDTO);
        return Results.success(result);
    }

    /**
     * 修改短链接
     * 1. 修改短链接的描述信息
     * 2. 修改短链接的有效期
     * 3. 修改短链接所属分组
     * short_uri 创建了唯一索引，可以根据该字段进行查询并修改
     * 由于表 t_link 进行了分片，所以需要添加分片键gid作为查询条件，避免全表扫描
     */
    @PutMapping
    public Result<Void> updateLink(@RequestBody LinkUpdateDTO linkUpdateDTO) {
        linkService.updateLink(linkUpdateDTO);
        return Results.success();
    }
}
