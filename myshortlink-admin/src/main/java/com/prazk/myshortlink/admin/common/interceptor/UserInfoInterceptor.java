package com.prazk.myshortlink.admin.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.prazk.myshortlink.admin.common.context.UserContext;
import com.prazk.myshortlink.admin.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.prazk.myshortlink.common.convention.constant.HttpHeadersConstant.USER_INFO_HEADER;

/**
 * 启用网关时，将用户信息加入到UserContext中
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "short-link.admin.enable-gateway", havingValue = "true")
@RequiredArgsConstructor
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader(USER_INFO_HEADER);
        if (StrUtil.isNotBlank(username)) {
            UserContext.setUser(User.builder().username(username).build());
//            log.info("userInfo = {}", UserContext.getUser());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 释放资源
        UserContext.removeUser();
    }
}
