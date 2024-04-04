package com.prazk.myshortlink.project.common.constant;

import java.time.Duration;

public class RedisConstant {
    /**
     * 短链接跳转长链接key前缀
     */
    public static final String GOTO_SHORT_LINK_KEY_PREFIX = "goto:slink:";
    /**
     * 短链接跳转长链接锁前缀
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY_PREFIX = "lock:goto:slink:";
    /**
     * 设置为永久有效时，短链接跳转长链接key超时时间
     */
    public static final Duration GOTO_SHORT_LINK_KEY_PERMANENT_DURATION = Duration.ofDays(30L);
    /**
     * 短链接跳转长链接key，缓存空值超时时间
     */
    public static final Duration GOTO_SHORT_LINK_EMPTY_VALUE_DURATION = Duration.ofSeconds(30L);
}
