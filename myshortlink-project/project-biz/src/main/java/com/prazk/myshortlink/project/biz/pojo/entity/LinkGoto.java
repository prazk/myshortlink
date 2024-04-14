package com.prazk.myshortlink.project.biz.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkGoto {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //短链接
    private String shortUri;
    //分组标识
    private String gid;
}
