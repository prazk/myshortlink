package com.prazk.myshortlink.admin.common.interceptor;

import cn.hutool.json.JSONUtil;
import com.prazk.myshortlink.admin.common.constant.RedisCacheConstant;
import com.prazk.myshortlink.admin.common.context.UserContext;
import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.admin.common.convention.exception.ClientException;
import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.common.convention.result.Results;
import com.prazk.myshortlink.admin.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 登录拦截校验
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 直接放行【用户注册接口】
        if (request.getRequestURI().equals("/api/short-link/admin/v1/user") && request.getMethod().equals("POST")) {
            return true;
        }

        String token = request.getHeader("token");
        String username = request.getHeader("username");

        String key = RedisCacheConstant.TOKEN_USER_LOGIN_PREFIX + username;
        String token1 = (String) stringRedisTemplate.opsForHash().get(key, "token");

        // 校验登录状态
        if (token == null || !token.equals(token1)) {
            Result<Void> result = Results.failure(new ClientException(BaseErrorCode.USER_NOT_LOGIN));
            String json = JSONUtil.toJsonStr(result);

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return false;
        }

        // 用户已登录
        UserContext.setUser(User.builder().username(username).build());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 释放资源
        UserContext.removeUser();
    }
}
