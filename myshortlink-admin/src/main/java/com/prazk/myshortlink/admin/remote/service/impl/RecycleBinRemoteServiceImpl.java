package com.prazk.myshortlink.admin.remote.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.remote.service.RecycleBinRemoteService;
import com.prazk.myshortlink.admin.service.GroupService;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Deprecated
@RequiredArgsConstructor
public class RecycleBinRemoteServiceImpl implements RecycleBinRemoteService {

    private final GroupService groupService;

    /**
     * 分页查询短链接接口
     */
    @Override
    public Result<IPage<RecyclePageVO>> pageRecycleBin(RecyclePageDTO recyclePageDTO) {
        // 1. 查询当前用户下的所有gid
        List<String> gidList = groupService.getGroups().stream().map(GroupVO::getGid).toList();

        // 2. 调用中台分页查询回收站短链接
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", gidList);
        requestMap.put("page", recyclePageDTO.getCurrent());
        requestMap.put("pageSize", recyclePageDTO.getSize());

        String result = HttpUtil.get("http://localhost:8089/api/short-link/project/recycle-bin/page", requestMap);
        return JSON.parseObject(result, new TypeReference<Result<IPage<RecyclePageVO>>>() {});
    }
}
