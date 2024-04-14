package com.prazk.myshortlink.admin.remote.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;

/**
 * 回收站远程调用服务
 */
@Deprecated
public interface RecycleBinRemoteService {

    /**
     * 分页查询回收站接口
     */
    Result<IPage<RecyclePageVO>> pageRecycleBin(RecyclePageDTO recyclePageDTO);
}
