package com.prazk.myshortlink.project.controller;

import com.prazk.myshortlink.project.common.convention.result.Result;
import com.prazk.myshortlink.project.common.convention.result.Results;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/project/recycle-bin")
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
}
