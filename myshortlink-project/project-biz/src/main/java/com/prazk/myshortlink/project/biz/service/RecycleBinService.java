package com.prazk.myshortlink.project.biz.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleRecoverDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;

public interface RecycleBinService {

    void add(RecycleAddDTO recycleAddDTO);

    void delete(RecycleDeleteDTO recycleDeleteDTO);

    void recover(RecycleRecoverDTO recycleRecoverDTO);

    Page<RecyclePageVO> page(RecyclePageDTO linkPageDTO);
}
