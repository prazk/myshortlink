package com.prazk.myshortlink.project.api.client;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.api.config.DefaultFeignConfig;
import com.prazk.myshortlink.project.pojo.dto.*;
import com.prazk.myshortlink.project.pojo.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 远程调用短链接接口Feign客户端
 */
@FeignClient(value = "short-link-project", configuration = DefaultFeignConfig.class)
public interface LinkClient {
    /**
     * 创建短链接
     */
    @PostMapping("/short-link/project")
    Result<LinkAddVO> addLink(@RequestBody LinkAddDTO linkAddDTO);

    /**
     * 短链接分页查询
     */
    @GetMapping("/short-link/project/page")
    Result<Page<LinkPageVO>> pageLink(@SpringQueryMap Map<String, Object> param);

    /**
     * 查询用户的所有分组的短链接数量
     */
    @GetMapping("/short-link/project/count")
    Result<List<LinkCountVO>> listLinkCount(@SpringQueryMap Map<String, Object> params);

    /**
     * 修改短链接
     * 1. 修改短链接的描述信息
     * 2. 修改短链接的有效期
     * 3. 修改短链接所属分组
     * short_uri 创建了唯一索引，可以根据该字段进行查询并修改
     * 由于表 t_link 进行了分片，所以需要添加分片键gid作为查询条件，避免全表扫描
     */
    @PutMapping("/short-link/project")
    Result<Void> updateLink(@RequestBody LinkUpdateDTO linkUpdateDTO);

    /**
     * 查询指定日期内总的PV、UV、IP，以及每天的PV、UV、IP
     */
    @GetMapping("/short-link/project/stats")
    Result<LinkStatsVO> getStats(@SpringQueryMap Map<String, Object> params);

    /**
     * 分页查询查询访问日志
     */
    @GetMapping("/short-link/project/stats/logs")
    Result<Page<LinkStatsLogsVO>> getLogs(@SpringQueryMap Map<String, Object> params);

    /**
     * 将短链接放入回收站
     */
    @PostMapping("/short-link/project/recycle-bin/save")
    Result<Void> addRecycleBin(@RequestBody RecycleAddDTO recycleAddDTO);

    /**
     * 彻底删除回收站的短链接
     */
    @DeleteMapping("/short-link/project/recycle-bin")
    Result<Void> deleteRecycleBin(@SpringQueryMap Map<String, Object> params);

    /**
     * 恢复回收站中的短链接
     */
    @PostMapping("/short-link/project/recycle-bin/recover")
    Result<Void> recoverRecycleBin(@RequestBody RecycleRecoverDTO recycleRecoverDTO);

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/short-link/project/recycle-bin/page")
    Result<Page<RecyclePageVO>> pageRecycleBin(@SpringQueryMap Map<String, Object> params);

    /**
     * 根据链接查询网页标题
     */
    @GetMapping("/short-link/project/title")
    Result<String> selectTitle(@SpringQueryMap Map<String, Object> params);
}
