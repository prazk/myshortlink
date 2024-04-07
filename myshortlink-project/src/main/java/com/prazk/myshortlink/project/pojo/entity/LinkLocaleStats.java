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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkLocaleStats {
    //ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //完整短链接
    private String shortUri;
    //日期
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate accessDate;
    //访问量
    private Integer cnt;
    //省份名称
    private String province;
    //市名称
    private String city;
    //城市的adcode编码
    private String adcode;
    //国家标识
    private String country;
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
