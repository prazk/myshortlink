package com.prazk.myshortlink.admin.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkPageVO;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接远程调用服务
 */
public interface ShortLinkRemoteService {

    /**
     * 调用中台的短链接分页查询功能
     */
    default Result<IPage<LinkPageVO>> page(LinkPageDTO linkPageDTO) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", linkPageDTO.getGid());
        requestMap.put("page", linkPageDTO.getPage());
        requestMap.put("pageSize", linkPageDTO.getPageSize());
        // 响应结果：JSON字符串
        String result = HttpUtil.get("http://localhost:8089/api/short-link/link/v1/link/page", requestMap);

        return JSON.parseObject(result, new TypeReference<Result<IPage<LinkPageVO>>>() {});
    }

    /**
     * 调用中台的创建短链接功能
     */
    default Result<LinkAddVO> addLink(LinkAddDTO linkAddDTO) {
        String requestJson = JSON.toJSONString(linkAddDTO);

        // 响应结果：JSON字符串
        String result = HttpUtil.post("http://localhost:8089/api/short-link/link/v1/link", requestJson);
        return JSON.parseObject(result, new TypeReference<Result<LinkAddVO>>() {});
    }
}
