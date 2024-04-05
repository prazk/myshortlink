package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/recycle-bin")
public class RecycleBinController {

    private final ShortLinkRemoteService shortLinkRemoteService;

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
}
