package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.common.convention.result.Results;
import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/admin/v1/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping
    public Result<Void> saveGroup(@RequestBody GroupCreateDTO groupCreateDTO) {
        groupService.saveGroup(groupCreateDTO);
        return Results.success();
    }

    /**
     * 查询所有分组：根据username查询
     */
    @GetMapping
    public Result<List<GroupVO>> getGroups() {
        List<GroupVO> list = groupService.getGroups();
        return Results.success(list);
    }

}