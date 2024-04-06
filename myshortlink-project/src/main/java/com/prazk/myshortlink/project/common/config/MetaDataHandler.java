package com.prazk.myshortlink.project.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mybatis-plus 自动填充
 */
@Component
public class MetaDataHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "accessDate", LocalDate::now, LocalDate.class);
        this.strictInsertFill(metaObject, "accessHour", () -> LocalDateTime.now().getHour(), Integer.class);
        this.strictInsertFill(metaObject, "accessWeekday", () -> LocalDate.now().getDayOfWeek().getValue(), Integer.class);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "delFlag", () -> CommonConstant.NOT_DELETED, Integer.class);
        this.strictInsertFill(metaObject, "enableStatus", () -> CommonConstant.HAS_ENABLED, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, "accessDate", LocalDate::now, LocalDate.class);
        this.strictUpdateFill(metaObject, "accessHour", () -> LocalDateTime.now().getHour(), Integer.class);
        this.strictUpdateFill(metaObject, "accessWeekday", () -> LocalDate.now().getDayOfWeek().getValue(), Integer.class);
    }
}
