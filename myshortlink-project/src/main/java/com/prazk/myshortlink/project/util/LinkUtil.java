package com.prazk.myshortlink.project.util;

import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.common.enums.ValidDateTypeEnum;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.net.UnknownHostException;

public class LinkUtil {
    private static final String UNKNOWN = "Unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    // 客户端与服务器同为一台机器，获取的 ip 有时候是 ipv6 格式
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";

    /**
     * 计算自定义有效期短链接的redis超时时间
     */
    public static Duration getLinkCustomExpireDuration(@NotNull LocalDateTime validDate) {
        Assert.notNull(validDate, "validDate must not be null");
        Duration permanent = RedisConstant.GOTO_SHORT_LINK_KEY_PERMANENT_DURATION;
        Duration customized = Duration.between(LocalDateTime.now(), validDate);
        return customized.isNegative() ? Duration.ZERO :
                permanent.compareTo(customized) < 0 ? permanent : customized;
    }

    /**
     * 根据短连接过期种类，获取短链接redis超时时间
     */
    public static Duration getLinkExpireDuraion(ValidDateTypeEnum typeEnum, LocalDateTime validDate) {
        return switch (typeEnum) {
            case PERMANENT -> RedisConstant.GOTO_SHORT_LINK_KEY_PERMANENT_DURATION;
            case CUSTOMIZED ->  getLinkCustomExpireDuration(validDate);
        };
    }

    /**
     * 获取真实IP
     */
    public static String getActualIP(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IP.equalsIgnoreCase(ip) || LOCALHOST_IPV6.equalsIgnoreCase(ip)) {
                // 根据网卡取本机配置的 IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (iNet != null)
                    ip = iNet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，分割出第一个 IP
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(SEPARATOR) > 0) {
                ip = ip.substring(0, ip.indexOf(SEPARATOR));
            }
        }
        return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IP : ip;
    }
}
