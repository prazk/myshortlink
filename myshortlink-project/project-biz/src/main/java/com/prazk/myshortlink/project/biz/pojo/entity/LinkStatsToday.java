package com.prazk.myshortlink.project.biz.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkStatsToday {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //短链接
    private String shortUri;
    //日期
    @TableField(fill = FieldFill.INSERT)
    private LocalDate accessDate;
    //今日PV
    private Integer todayPv;
    //今日UV
    private Integer todayUv;
    //今日IP数
    private Integer todayUip;
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

