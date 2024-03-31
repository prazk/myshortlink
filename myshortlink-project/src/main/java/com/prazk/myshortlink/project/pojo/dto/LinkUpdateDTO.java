package com.prazk.myshortlink.project.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkUpdateDTO {
    //短链接
    private String shortUri;
    //分组标识
    private String gid;
    //修改后的分组标识
    private String newGid;
    //描述
    private String description;
    //有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime validDate;
    //有效期类型
    private Integer validDateType;
}
