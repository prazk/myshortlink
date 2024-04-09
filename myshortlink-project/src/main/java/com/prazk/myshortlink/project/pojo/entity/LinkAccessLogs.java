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
public class LinkAccessLogs {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //短链接
    private String shortUri;
    //分组标识
    private String gid;
    //用户信息
    private String user;
    //浏览器
    private String browser;
    //操作系统
    private String os;
    //访问设备类型
    private Integer device;
    //IP
    private String ip;
    //省份
    private String province;
    //市名称
    private String city;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //删除标识 0：未删除 1：已删除
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}

