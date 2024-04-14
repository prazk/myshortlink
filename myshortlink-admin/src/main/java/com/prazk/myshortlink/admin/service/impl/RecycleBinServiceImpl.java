package com.prazk.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.service.GroupService;
import com.prazk.myshortlink.admin.service.RecycleBinService;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.api.client.LinkClient;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final LinkClient linkClient;
    private final GroupService groupService;

    @Override
    public Result<Page<RecyclePageVO>> page(RecyclePageDTO recyclePageDTO) {
        // 1. 查询当前用户下的所有gid
        List<String> gidList = groupService.getGroups().stream().map(GroupVO::getGid).toList();
        recyclePageDTO.setGid(gidList);

        // 2. 调用中台分页查询回收站短链接
        Map<String, Object> map = BeanUtil.beanToMap(recyclePageDTO);
        return linkClient.pageRecycleBin(map);
    }
}
