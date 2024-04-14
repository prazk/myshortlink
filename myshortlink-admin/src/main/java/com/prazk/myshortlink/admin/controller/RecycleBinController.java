package com.prazk.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.admin.service.RecycleBinService;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.api.client.LinkClient;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleRecoverDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("short-link/admin/recycle-bin")
public class RecycleBinController {

    private final LinkClient linkClient;
    private final RecycleBinService recycleBinService;

    /**
     * 调用中台短链接加入回收站接口
     */
    @PostMapping("/save")
    public Result<Void> add(@RequestBody RecycleAddDTO recycleAddDTO) {
        return linkClient.addRecycleBin(recycleAddDTO);
    }

    /**
     * 调用中台彻底删除回收站的短链接接口
     */
    @DeleteMapping
    public Result<Void> delete(RecycleDeleteDTO recycleDeleteDTO) {
        return linkClient.deleteRecycleBin(BeanUtil.beanToMap(recycleDeleteDTO));
    }

    /**
     * 调用中台恢复回收站中的短链接接口
     */
    @PostMapping("/recover")
    public Result<Void> recover(@RequestBody RecycleRecoverDTO recycleRecoverDTO) {
        return linkClient.recoverRecycleBin(recycleRecoverDTO);
    }

    /**
     * 调用中台分页查询回收站接口
     * 1. 查询当前用户下的所有gid
     * 2. 调用中台分页查询回收站短链接
     */
    @GetMapping("/page")
    public Result<Page<RecyclePageVO>> page(RecyclePageDTO recyclePageDTO) {
        return recycleBinService.page(recyclePageDTO);
    }
}
