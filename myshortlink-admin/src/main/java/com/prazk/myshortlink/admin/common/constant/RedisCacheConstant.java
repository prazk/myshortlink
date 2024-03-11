package com.prazk.myshortlink.admin.common.constant;

/**
 * 缓存相关常量类
 */
public class RedisCacheConstant {
   /**
    * 用户注册分布式锁，key设置为username
    */
   public static final String LOCK_USER_REGISTER_PREFIX = "short-link:user-register:lock:";
}
