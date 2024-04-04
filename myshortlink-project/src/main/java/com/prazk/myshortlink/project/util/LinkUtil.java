package com.prazk.myshortlink.project.util;

import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.common.enums.ValidDateTypeEnum;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;

public class LinkUtil {

    /**
     * 计算自定义有效期短链接的redis超时时间
     */
    public static Duration getLinkCustomExpireDuration(@NotNull LocalDateTime validDate) {
        Assert.notNull(validDate, "validDate must not be null");
        Duration permanent = RedisConstant.GOTO_SHORT_LINK_KEY_PERMANENT_DURATION;
        Duration customized = Duration.between(LocalDateTime.now(), validDate);
        return customized.isNegative() ? Duration.ZERO :
                permanent.compareTo(customized) < 0 ? permanent : customized;
    }

    /**
     * 根据短连接过期种类，获取短链接redis超时时间
     */
    public static Duration getLinkExpireDuraion(ValidDateTypeEnum typeEnum, LocalDateTime validDate) {
        return switch (typeEnum) {
            case PERMANENT -> RedisConstant.GOTO_SHORT_LINK_KEY_PERMANENT_DURATION;
            case CUSTOMIZED ->  getLinkCustomExpireDuration(validDate);
        };
    }
}
