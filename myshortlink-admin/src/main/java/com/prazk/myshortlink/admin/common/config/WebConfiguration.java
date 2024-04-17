package com.prazk.myshortlink.admin.common.config;

import com.prazk.myshortlink.admin.common.interceptor.LoginInterceptor;
import com.prazk.myshortlink.admin.common.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

//    private final LoginInterceptor loginInterceptor;
//    private final UserInfoInterceptor userInfoInterceptor;
    private final HandlerInterceptor handlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (handlerInterceptor instanceof LoginInterceptor) {
            log.info("注册的拦截器是：LoginInterceptor");
            registry.addInterceptor(handlerInterceptor)
                    .excludePathPatterns("/short-link/admin/user/login") // 登录接口
                    .excludePathPatterns("/short-link/admin/user/has-username") // 用户存在
            ;
        } else if (handlerInterceptor instanceof UserInfoInterceptor){
            log.info("注册的拦截器是：UserInfoInterceptor");
            registry.addInterceptor(handlerInterceptor);
        }
    }
}
