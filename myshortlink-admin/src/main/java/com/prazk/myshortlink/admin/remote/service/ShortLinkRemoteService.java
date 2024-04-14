package com.prazk.myshortlink.admin.remote.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.pojo.dto.*;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接远程调用服务
 */
@Deprecated
public interface ShortLinkRemoteService {

    /**
     * 调用中台的短链接分页查询功能
     */
    default Result<IPage<LinkPageVO>> pageLink(LinkPageDTO linkPageDTO) {
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

    /**
     * 调用中台查询用户的所有分组的短链接数量接口
     */
    default Result<List<LinkCountVO>> listLinkCount(LinkCountDTO linkCountDTO) {
        Map<String, Object> map = BeanUtil.beanToMap(linkCountDTO, false, true);

        // 响应结果：JSON字符串
        String result = HttpUtil.get("http://localhost:8089/api/short-link/link/v1/link/count", map);
        return JSON.parseObject(result, new TypeReference<Result<List<LinkCountVO>>>() {});
    }

    /**
     * 调用中台修改短链接功能
     */
    default Result<Void> updateLink(LinkUpdateDTO linkUpdateDTO) {
        String body = JSON.toJSONString(linkUpdateDTO);

        String result = HttpUtil
                .createRequest(Method.PUT, "http://localhost:8089/api/short-link/link/v1/link")
                .body(body,"application/json")
                .execute()
                .body();

        return JSON.parseObject(result, new TypeReference<Result<Void>>() {});
    }

    /**
     * 调用中台短链接加入回收站功能
     */
    default Result<Void> addRecycleBin(RecycleAddDTO recycleAddDTO) {
        String requestJson = JSON.toJSONString(recycleAddDTO);

        String result = HttpUtil.post("http://localhost:8089/api/short-link/project/recycle-bin/save", requestJson);
        return JSON.parseObject(result, new TypeReference<Result<Void>>() {});
    }

    /**
     * 调用中台彻底删除回收站的短链接接口
     */
    default Result<Void> deleteRecycleBin(RecycleDeleteDTO recycleDeleteDTO) {
        String gid = recycleDeleteDTO.getGid();
        String shortUri = recycleDeleteDTO.getShortUri();

        String result = HttpUtil
                .createRequest(
                        Method.DELETE,
                        "http://localhost:8089/api/short-link/project/recycle-bin?gid=" + gid + "&shortUri=" + shortUri)
                .execute()
                .body();

        return JSON.parseObject(result, new TypeReference<Result<Void>>() {});
    }

    default Result<Void> recoverRecycleBin(RecycleRecoverDTO recycleRecoverDTO) {
        String requestJson = JSON.toJSONString(recycleRecoverDTO);

        String result = HttpUtil.post("http://localhost:8089/api/short-link/project/recycle-bin/recover", requestJson);
        return JSON.parseObject(result, new TypeReference<Result<Void>>() {});
    }
}
