package com.prazk.myshortlink.admin.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupVO {
    //分组ID
    private String gid;
    //分组名称
    private String name;
    // 当前分组下的短链接数量
    private Integer count = 0;
    // 自定义排序
    @TableField(fill = FieldFill.INSERT)
    private Integer sortOrder;

}
