package com.prazk.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prazk.myshortlink.project.pojo.dto.*;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;

public interface RecycleBinService {

    void add(RecycleAddDTO recycleAddDTO);

    void delete(RecycleDeleteDTO recycleDeleteDTO);

    void recover(RecycleRecoverDTO recycleRecoverDTO);

    IPage<RecyclePageVO> page(RecyclePageDTO linkPageDTO);
}
