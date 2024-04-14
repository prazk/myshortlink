package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;

public interface RecycleBinService {
    Result<Page<RecyclePageVO>> page(RecyclePageDTO recyclePageDTO);
}
