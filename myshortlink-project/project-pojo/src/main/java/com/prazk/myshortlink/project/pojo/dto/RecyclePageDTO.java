package com.prazk.myshortlink.project.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecyclePageDTO {
    // 用户所有分组的gid
    private List<String> gid;
    // 页码
    private Integer current;
    // 页大小
    private Integer size;
}
