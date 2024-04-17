package com.prazk.myshortlink.common.convention.constant;

public class RedisCacheConstant {
    /**
     * 用户流量风控key
     */
    public static final String USER_FLOW_RISK_PREFIX = "short-link:user-flow-risk:";
    /**
     * 用户注册分布式锁，key设置为username
     */
    public static final String LOCK_USER_REGISTER_PREFIX = "short-link:user-register:lock:";
    /**
     * 添加分组时，分组数量的分布式锁
     */
    public static final String LOCK_GROUP_COUNT_PREFIX = "short-link:group-count:lock:";
    /**
     * 用户登录token，key设置为username
     */
    public static final String TOKEN_USER_LOGIN_PREFIX = "short-link:user-login:token:";
    /**
     * 用户登录状态持续时间：单位 分钟
     */
    public static final int DURATION_USER_LOGIN = 365 * 24 * 60;
}
