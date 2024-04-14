package com.prazk.myshortlink.project.biz.aspect;

import cn.hutool.core.util.URLUtil;
import com.prazk.myshortlink.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.biz.annotation.DomainWhiteList;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DomainWhiteListAspect {
    @Pointcut("execution(* com.prazk.myshortlink.project.biz.service.*.* (..)) && @annotation(com.prazk.myshortlink.project.biz.annotation.DomainWhiteList)")
    public void domainWhiteListPointcut() {}

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext context;

    private boolean whiteListEnabled;

    @PostConstruct
    public void init() {
        // 检查属性是否存在
        if (environment.containsProperty("project.goto-domain.white-list.enabled")) {
            whiteListEnabled = Boolean.parseBoolean(environment.getProperty("project.goto-domain.white-list.enabled"));
        } else {
            // 如果属性不存在，默认设置为false
            whiteListEnabled = false;
        }
    }

    @Around("domainWhiteListPointcut()")
    public Object domainWhiteList(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!whiteListEnabled) {
            return joinPoint.proceed();
        }
        log.info("检查域名是否在白名单内");

        // 获取注解@DomainWhiteList的值allowedDomains
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DomainWhiteList annotation = signature.getMethod().getAnnotation(DomainWhiteList.class);
        String[] allowedDomains = annotation.allowedDomains();

        // 根据配置类的属性获取白名单
        Class<?> config = annotation.config();
        String[] allowedDomains2 = {};
        if (!config.equals(Void.class)) {
            Object properties = context.getBean(config);
            allowedDomains2 = (String[]) config.getDeclaredMethod("getDomains").invoke(properties);
        }

        // 获取请求的域名
        Object firstArg = joinPoint.getArgs()[0];
        Method getMethod = firstArg.getClass().getMethod("getOriginUri");
        String originUri = (String) getMethod.invoke(firstArg);
        if (originUri == null) {
            throw new ClientException("请输入跳转链接");
        }
        String host = URLUtil.getHost(URLUtil.url(originUri)).getHost();

        // 检查请求的域名是否在白名单内
        StringBuilder sb = new StringBuilder();
        for (String domain : allowedDomains) {
            sb.append(domain).append(", ");
            if (host.equals(domain)) {
                return joinPoint.proceed();
            }
        }

        for (String domain : allowedDomains2) {
            sb.append(domain).append(", ");
            if (host.equals(domain)) {
                return joinPoint.proceed();
            }
        }

        log.info("域名不在白名单内");
        String errorMessage = "不支持跳转到该短链接域名，请使用以下域名：" + sb;
        throw new ClientException(errorMessage);

    }
}
