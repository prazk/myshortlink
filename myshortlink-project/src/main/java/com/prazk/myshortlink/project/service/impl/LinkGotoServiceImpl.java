package com.prazk.myshortlink.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.LinkGotoMapper;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.service.LinkGotoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGoto> implements LinkGotoService {

    private final RBloomFilter<String> shortLinkGenerationBloomFilter;
    private final LinkMapper linkMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @SneakyThrows
    @Override
    public void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response) {
        // 先查询布隆过滤器，再查询数据库
        String shortUri = linkRestoreDTO.getShortUri();
        // RBloomFilter是线程安全的
        if (!shortLinkGenerationBloomFilter.contains(shortUri)) {
            log.info("布隆过滤器未命中");
            // 布隆过滤器不存在，则实际一定不存在，说明是无效短链接
            throw new ClientException(BaseErrorCode.LINK_NOT_EXISTS_ERROR);
        }
        // TODO 布隆过滤器存在，则实际可能不存在，仍然存在缓存穿透问题
        // 查询Redis缓存：key【短链接】，value【原始链接】
        String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
        String originUri = stringRedisTemplate.opsForValue().get(key);
        if (!StrUtil.isBlank(originUri)) {
            log.info("缓存命中");
            // 重定向结果
            response.sendRedirect(originUri);
            return;
        }
        log.info("缓存未命中");
        // 缓存未命中，访问数据库
        // 存在缓存击穿问题：一个热点key失效，同时大量请求访问这个key，导致大量请求访问到数据库
        // 分布式锁解决缓存击穿问题，锁对象是短链接
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_GOTO_SHORT_LINK_KEY_PREFIX + shortUri);
        if (lock.tryLock(200, TimeUnit.MILLISECONDS)) { // 设置重试时间：200ms
            try {
                // Thread.sleep(10000); // 模拟重建时间长的场景
                // 获取锁成功，让这个线程去访问数据库重建缓存，阻塞其他线程
                // 通过中间表的分片键【short_uri】，拿到短链接表的分片键【gid】
                LambdaQueryWrapper<LinkGoto> linkGotoWrapper = new LambdaQueryWrapper<>();
                linkGotoWrapper.eq(LinkGoto::getShortUri, shortUri);
                LinkGoto linkGoto = getOne(linkGotoWrapper);
                if (linkGoto == null) {
                    log.info("数据库未命中");
                    throw new ClientException(BaseErrorCode.LINK_NOT_EXISTS_ERROR);
                }
                String gid = linkGoto.getGid();
                // 根据短链接表的分片键【gid】，查询数据库，得到原始链接
                LambdaQueryWrapper<Link> linkWrapper = new LambdaQueryWrapper<>();
                linkWrapper.eq(Link::getGid, gid)
                        .eq(Link::getShortUri, shortUri)
                        .eq(Link::getEnableStatus, CommonConstant.HAS_ENABLED)
                        .eq(Link::getDelFlag, CommonConstant.NOT_DELETED);
                Link link = linkMapper.selectOne(linkWrapper);
                if (link == null) {
                    log.info("数据库未命中");
                    throw new ClientException(BaseErrorCode.LINK_NOT_EXISTS_ERROR);
                }
                // 设置缓存以及超时时间
                log.info("数据库命中");
                stringRedisTemplate.opsForValue().set(key, link.getOriginUri(), RedisConstant.GOTO_SHORT_LINK_KEY_DURATION);
                response.sendRedirect(link.getOriginUri());
            } finally {
                lock.unlock();
            }
        } else {
            throw new ClientException(BaseErrorCode.SERVICE_BUSY_ERROR);
        }
    }
}
