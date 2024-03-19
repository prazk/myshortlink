package com.prazk.myshortlink.admin.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.prazk.myshortlink.admin.common.constant.GroupConstant.SORT_ORDER_MAX_LENGTH;

/**
 * Mybatis-plus 自动填充
 */
@Component
public class MetaDataHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
        // 分组排序
        this.strictInsertFill(metaObject, "sortOrder", () -> SORT_ORDER_MAX_LENGTH, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
