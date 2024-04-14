package com.prazk.myshortlink.project.biz.common.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration // 配置类管理第三方bean
public class RBloomFilterConfiguration {

    /**
     * 防止生成已存在的短链接访问数据库的布隆过滤器
     */
    @Bean // 将方法返回对象加入IOC容器
    public RBloomFilter<String> shortLinkGenerationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortLinkGenerationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}