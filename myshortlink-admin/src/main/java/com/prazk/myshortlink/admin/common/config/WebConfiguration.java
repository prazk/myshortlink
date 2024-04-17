package com.prazk.myshortlink.admin.common.config;

import com.prazk.myshortlink.admin.common.interceptor.UserInfoInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

//    private final LoginInterceptor loginInterceptor;
    private final UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .excludePathPatterns("/short-link/admin/user/login") // 登录接口
//                .excludePathPatterns("/short-link/admin/user/has-username") // 用户存在
//        ;
        registry.addInterceptor(userInfoInterceptor);
    }
}
