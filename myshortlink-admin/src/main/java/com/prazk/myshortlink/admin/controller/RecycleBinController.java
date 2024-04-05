package com.prazk.myshortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleRecoverDTO;
import com.prazk.myshortlink.admin.remote.pojo.vo.RecyclePageVO;
import com.prazk.myshortlink.admin.remote.service.RecycleBinRemoteService;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/recycle-bin")
public class RecycleBinController {

    private final ShortLinkRemoteService shortLinkRemoteService;
    private final RecycleBinRemoteService recycleBinRemoteService;

    /**
     * 调用中台短链接加入回收站接口
     */
    @PostMapping("/save")
    public Result<Void> add(@RequestBody RecycleAddDTO recycleAddDTO) {
        return shortLinkRemoteService.addRecycleBin(recycleAddDTO);
    }

    /**
     * 调用中台彻底删除回收站的短链接接口
     */
    @DeleteMapping
    public Result<Void> delete(RecycleDeleteDTO recycleDeleteDTO) {
        return shortLinkRemoteService.deleteRecycleBin(recycleDeleteDTO);
    }

    /**
     * 调用中台恢复回收站中的短链接接口
     */
    @PostMapping("/recover")
    public Result<Void> recover(@RequestBody RecycleRecoverDTO recycleRecoverDTO) {
        return shortLinkRemoteService.recoverRecycleBin(recycleRecoverDTO);
    }

    /**
     * 分页查询回收站接口
     * 1. 查询当前用户下的所有gid
     * 2. 调用中台分页查询回收站短链接
     */
    @GetMapping("/page")
    public Result<IPage<RecyclePageVO>> page(RecyclePageDTO recyclePageDTO) {
        return recycleBinRemoteService.pageRecycleBin(recyclePageDTO);
    }
}
