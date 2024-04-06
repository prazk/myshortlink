package com.prazk.myshortlink.project.pojo.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkAccessStats {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //短链接
    private String shortUri;
    //访问人次
    private Integer pv;
    //访问人数
    private Integer uv;
    //独立IP数
    private Integer uip;
    //日期
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate accessDate;
    //小时
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer accessHour;
    //星期几，1-7
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer accessWeekday;
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

