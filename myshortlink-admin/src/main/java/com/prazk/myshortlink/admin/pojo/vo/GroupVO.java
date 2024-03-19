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
    //创建人用户名
    private String username;
    // 自定义排序
    @TableField(fill = FieldFill.INSERT)
    private Integer sortOrder;

}
