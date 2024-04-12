package com.prazk.myshortlink.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DomainWhiteList {
    /**
     * 域名白名单
     */
    String[] allowedDomains() default {};
    /**
     * 根据配置类的属性domains获取白名单
     */
    Class<?> config() default Void.class;
}
