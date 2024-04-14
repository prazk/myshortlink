package com.prazk.myshortlink.project.biz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.common.convention.result.Results;
import com.prazk.myshortlink.project.biz.service.RecycleBinService;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleRecoverDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("short-link/project/recycle-bin")
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 将短链接放入回收站
     */
    @PostMapping("/save")
    public Result<Void> add(@RequestBody RecycleAddDTO recycleAddDTO) {
        recycleBinService.add(recycleAddDTO);
        return Results.success();
    }

    /**
     * 彻底删除回收站的短链接
     */
    @DeleteMapping
    public Result<Void> delete(RecycleDeleteDTO recycleDeleteDTO) {
        recycleBinService.delete(recycleDeleteDTO);
        return Results.success();
    }

    /**
     * 恢复回收站中的短链接
     */
    @PostMapping("/recover")
    public Result<Void> recover(@RequestBody RecycleRecoverDTO recycleRecoverDTO) {
        recycleBinService.recover(recycleRecoverDTO);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/page")
    public Result<Page<RecyclePageVO>> page(RecyclePageDTO recyclePageDTO) {
        Page<RecyclePageVO> result = recycleBinService.page(recyclePageDTO);
        return Results.success(result);
    }

}
