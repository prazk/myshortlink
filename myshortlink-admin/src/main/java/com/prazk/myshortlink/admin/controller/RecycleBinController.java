package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
