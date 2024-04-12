package com.prazk.myshortlink.admin.common.constant;

/**
 * 缓存相关常量类
 */
public class RedisCacheConstant {
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
