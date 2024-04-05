package com.prazk.myshortlink.admin.remote.pojo.dto;

import lombok.Data;

@Data
public class RecyclePageDTO {
    // 页码
    private Integer page;
    // 页大小
    private Integer pageSize;
}
