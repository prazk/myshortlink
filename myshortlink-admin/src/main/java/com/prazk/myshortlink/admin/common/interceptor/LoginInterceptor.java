package com.prazk.myshortlink.admin.common.interceptor;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.prazk.myshortlink.admin.common.constant.RedisCacheConstant;
import com.prazk.myshortlink.admin.common.context.UserContext;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.common.convention.exception.ClientException;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.common.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;


/**
 * 登录拦截校验
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private static final DefaultRedisScript<Long> FLOW_RISK_SCRIPT;

    static {
        FLOW_RISK_SCRIPT = new DefaultRedisScript<>();
        FLOW_RISK_SCRIPT.setLocation(new ClassPathResource("lua/user_req_risk_control.lua"));
        FLOW_RISK_SCRIPT.setResultType(Long.class);
    }

    @Value("${admin.flow-risk.expire-seconds}")
    private String expireSeconds;

    @Value("${admin.flow-risk.req-times}")
    private String reqTimes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info(request.getRequestURI());
        // 直接放行【用户注册接口】
        if (request.getRequestURI().equals("/short-link/admin/user") && request.getMethod().equals("POST")) {
            return true;
        }

        String token = request.getHeader("token");
        String username = request.getHeader("username");

        // 流量风控
        String riskKey = RedisCacheConstant.USER_FLOW_RISK_PREFIX + username;
        List<String> riskKeys = ListUtil.of(riskKey);
        Long flag = stringRedisTemplate.execute(FLOW_RISK_SCRIPT, riskKeys, reqTimes, expireSeconds);
        assert flag != null;
        if (flag.equals(0L)) {
            throw new ClientException("请求过于频繁，请" + expireSeconds + "秒后再试");
        }


        String loginTokenKey = RedisCacheConstant.TOKEN_USER_LOGIN_PREFIX + username;
        String token1 = (String) stringRedisTemplate.opsForHash().get(loginTokenKey, "token");

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
