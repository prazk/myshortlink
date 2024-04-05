package com.prazk.myshortlink.project.service;

import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleDeleteDTO;

public interface RecycleBinService {

    void add(RecycleAddDTO recycleAddDTO);

    void delete(RecycleDeleteDTO recycleDeleteDTO);
}
