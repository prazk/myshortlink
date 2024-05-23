package com.prazk.myshortlink.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSON;
import com.prazk.myshortlink.common.convention.constant.RedisCacheConstant;
import com.prazk.myshortlink.common.convention.result.GatewayErrorResult;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.prazk.myshortlink.common.convention.constant.HttpHeadersConstant.USER_INFO_HEADER;

/**
 * 网关过滤器工厂类，实现了登录校验与限流功能
 */
@Component
public class TokenValidateGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TokenValidateGatewayFilterFactory.Config> {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> FLOW_RISK_SCRIPT;

    static {
        FLOW_RISK_SCRIPT = new DefaultRedisScript<>();
        FLOW_RISK_SCRIPT.setLocation(new ClassPathResource("lua/user_req_risk_control.lua"));
        FLOW_RISK_SCRIPT.setResultType(Long.class);
    }

    @Value("${gateway.flow-risk.expire-seconds}")
    private String expireSeconds;

    @Value("${gateway.flow-risk.req-times}")
    private String reqTimes;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate) {
        super(Config.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Data
    public static class Config {
        private List<String> whitePathList;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String path = request.getPath().toString();

            // 放行用户注册接口
            if ("/short-link/admin/user".equals(path) && "POST".equals(request.getMethod().name())) {
                return chain.filter(exchange);
            }

            // 白名单校验：放行白名单中的接口
            List<String> whitePathList = config.whitePathList;
            if (!CollUtil.isEmpty(whitePathList) && whitePathList.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            String token = request.getHeaders().getFirst("token");
            String username = request.getHeaders().getFirst("username");

            // 流量风控
            String riskKey = RedisCacheConstant.USER_FLOW_RISK_PREFIX + username;
            List<String> riskKeys = ListUtil.of(riskKey);
            Long flag = stringRedisTemplate.execute(FLOW_RISK_SCRIPT, riskKeys, reqTimes, expireSeconds);
            assert flag != null;
            if (flag.equals(0L)) {
                // 503 服务器忙
                response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    GatewayErrorResult resultMessage = GatewayErrorResult.builder()
                            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                            .message("Frequent requests, please try again later")
                            .build();
                    return bufferFactory.wrap(JSON.toJSONString(resultMessage).getBytes());
                }));
            }

            String loginTokenKey = RedisCacheConstant.TOKEN_USER_LOGIN_PREFIX + username;
            String token1 = (String) stringRedisTemplate.opsForHash().get(loginTokenKey, "token");

            // 校验登录状态
            if (token == null || !token.equals(token1)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    GatewayErrorResult resultMessage = GatewayErrorResult.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Token validation error")
                            .build();
                    return bufferFactory.wrap(JSON.toJSONString(resultMessage).getBytes());
                }));
            }

            // 用户已登录：设置请求头信息
            ServerWebExchange serverWebExchange = exchange
                    .mutate()
                    .request(builder -> builder.header(USER_INFO_HEADER, username))
                    .build();

            return chain.filter(serverWebExchange);
        }, 0);
    }
}
