package com.prazk.myshortlink.project.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //域名
    private String domain;
    //短链接
    private String shortUri;
    //完整短链接
    private String fullShortUri;
    //原始链接
    private String originUri;
    //点击量
    private Integer clickNum;
    //分组标识
    private String gid;
    //启用标识 0：未启用 1：已启用
    @TableField(fill = FieldFill.INSERT)
    private Integer enableStatus;
    //创建类型 0：控制台 1：接口
    private Integer createdType;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    private LocalDateTime validDate;
    //描述
    private String description;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //修改时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标识 0：未删除 1：已删除
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
