package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupSortDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupUpdateDTO;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.service.GroupService;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("short-link/admin/group")
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

    /**
     * 短链接分组修改
     */
    @PutMapping
    public Result<Void> updateGroup(@RequestBody GroupUpdateDTO groupUpdateDTO) {
        groupService.updateGroup(groupUpdateDTO);
        return Results.success();
    }

    /**
     * 删除短链接分组：根据用户名和分组id删除
     * 逻辑删除，设置 delFlag为 true
     */
    @DeleteMapping
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 分组排序
     */
    @PutMapping("/sort")
    public Result<Void> sortGroup(@RequestBody List<GroupSortDTO> list) {
        groupService.sortGroup(list);
        return Results.success();
    }
}
