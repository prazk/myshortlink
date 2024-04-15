package com.prazk.myshortlink.project.pojo.dto;

import lombok.Data;

/**
 * 短链接分页查询 DTO
 * 根据分组id查询分页数据
 */
@Data
public class LinkPageDTO {
    // 分组id
    private String gid;
    // 页码
    private Integer current;
    // 页大小
    private Integer size;
    // 排序字段
    private String orderTag;
}
