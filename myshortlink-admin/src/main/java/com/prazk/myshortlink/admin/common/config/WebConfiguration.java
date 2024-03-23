package com.prazk.myshortlink.admin.common.config;

import com.prazk.myshortlink.admin.common.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/api/short-link/admin/v1/user/login") // 登录接口
                .excludePathPatterns("/api/short-link/admin/v1/user/has-username") // 用户存在
        ;
    }
}
