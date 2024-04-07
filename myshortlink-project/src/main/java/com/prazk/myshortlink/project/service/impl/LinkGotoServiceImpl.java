package com.prazk.myshortlink.project.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.common.enums.ValidDateTypeEnum;
import com.prazk.myshortlink.project.mapper.LinkAccessStatsMapper;
import com.prazk.myshortlink.project.mapper.LinkGotoMapper;
import com.prazk.myshortlink.project.mapper.LinkLocaleStatsMapper;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.entity.LinkAccessStats;
import com.prazk.myshortlink.project.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.pojo.entity.LinkLocaleStats;
import com.prazk.myshortlink.project.remote.resp.AmapIPLocale;
import com.prazk.myshortlink.project.service.LinkGotoService;
import com.prazk.myshortlink.project.util.LinkUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGoto> implements LinkGotoService {

    private final RBloomFilter<String> shortLinkGenerationBloomFilter;
    private final LinkMapper linkMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    @Value("${amap.region-stats.key}")
    private String amapRegionStatsKey;

    @SneakyThrows
    @Override
    public void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response) {
        // 先查询布隆过滤器，再查询数据库
        String shortUri = linkRestoreDTO.getShortUri();
        // RBloomFilter是线程安全的
        if (!shortLinkGenerationBloomFilter.contains(shortUri)) {
            log.info("布隆过滤器未命中");
            // 布隆过滤器不存在，则实际一定不存在，说明是无效短链接
            response.sendRedirect("/link/notfound");
            return;
        }
        // 布隆过滤器存在，则实际可能不存在，仍然存在缓存穿透问题，再采用缓存空值的方法解决缓存穿透问题
        // 但是对于大量请求，使用不同的且不存在于数据库的key进行访问，会缓存大量的空值数据
        // 查询Redis缓存：key【短链接】，value【原始链接】
        String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
        String originUri = stringRedisTemplate.opsForValue().get(key);
        if (!StrUtil.isBlank(originUri)) { // 不为null且不为空
            log.info("缓存命中");
            // 统计访问数据
            doStatistics(request, response, shortUri);
            // 重定向结果
            response.sendRedirect(originUri);
            return;
        }
        if ("".equals(originUri)) {
            log.info("查询到空数据");
            response.sendRedirect("/link/notfound");
            return;
        }
        // 查询到null，缓存未命中，访问数据库
        log.info("缓存未命中");
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
                    // 缓存空值，解决缓存穿透问题
                    stringRedisTemplate.opsForValue().set(key, "", RedisConstant.GOTO_SHORT_LINK_EMPTY_VALUE_DURATION);
                    response.sendRedirect("/link/notfound");
                    return;
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
                    // 缓存空值，解决缓存穿透问题
                    stringRedisTemplate.opsForValue().set(key, "", RedisConstant.GOTO_SHORT_LINK_EMPTY_VALUE_DURATION);
                    response.sendRedirect("/link/notfound");
                    return;
                }
                // 设置缓存以及超时时间
                log.info("数据库命中");
                Duration expire = LinkUtil.getLinkExpireDuraion(ValidDateTypeEnum.fromType(link.getValidDateType()), link.getValidDate());
                if (expire.equals(Duration.ZERO)) {
                    log.info("短链接已过期");
                    throw new ClientException(BaseErrorCode.LINK_EXPIRED_ERROR);
                }
                stringRedisTemplate.opsForValue().set(key, link.getOriginUri(), expire);
                // 统计访问数据
                doStatistics(request, response, shortUri);
                response.sendRedirect(link.getOriginUri());
            } finally {
                lock.unlock();
            }
        } else {
            throw new ClientException(BaseErrorCode.SERVICE_BUSY_ERROR);
        }
    }

    private void doStatistics(HttpServletRequest request, HttpServletResponse response, String shortUri) {
        try {
            // UV统计
            String uvKey = RedisConstant.STATS_UV_KEY_PREFIX + shortUri;
            Cookie[] cookies = request.getCookies();
            String value = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("uv")) {
                        value = cookie.getValue();
                        break;
                    }
                }
            }

            if (value == null) {
                value = IdUtil.fastSimpleUUID();
                Cookie uv = new Cookie("uv", value);
                uv.setMaxAge(3600 * 24 * 30);
                // 生成的Cookie是用户的唯一标识，即使被不同短链接路径共享，也没关系
                uv.setPath("/");
                response.addCookie(uv);
            }

            stringRedisTemplate.opsForHyperLogLog().add(uvKey, value);
            Integer uvCount = stringRedisTemplate.opsForHyperLogLog().size(uvKey).intValue();

            // IP统计
            String ipKey = RedisConstant.STATS_IP_KEY_PREFIX + shortUri;
            String actualIP = LinkUtil.getActualIP(request);
            if (!"unknown".equals(actualIP)) {
                stringRedisTemplate.opsForHyperLogLog().add(ipKey, actualIP);
            }
            Integer ipCount = stringRedisTemplate.opsForHyperLogLog().size(ipKey).intValue();

            // 地区统计
            Map<String, Object> reqParams = new HashMap<>();
            reqParams.put("key", amapRegionStatsKey);
            reqParams.put("ip", actualIP);
            String respBody = HttpUtil.get("https://restapi.amap.com/v3/ip", reqParams);
            AmapIPLocale amapIPLocale = JSONUtil.toBean(respBody, AmapIPLocale.class);
            if (amapIPLocale.getInfocode().equals("10000")) {
                String city = amapIPLocale.getCity().equals("[]") ? "未知" : amapIPLocale.getCity();
                String adcode = amapIPLocale.getAdcode().equals("[]") ? "未知" : amapIPLocale.getAdcode();
                String province = amapIPLocale.getProvince().equals("[]") ? "未知" : amapIPLocale.getProvince();

                LinkLocaleStats localeStats = LinkLocaleStats.builder()
                            .country("中国")
                            .province(province)
                            .adcode(adcode)
                            .city(city)
                            .shortUri(shortUri)
                            .build();

                linkLocaleStatsMapper.recordLocalAccessStats(localeStats);
            } else {
                log.error("调用高德开放地图IP定位接口失败，错误信息：{}", amapIPLocale.getInfo());
            }

            // PV统计
            LinkAccessStats accessStats = LinkAccessStats.builder()
                    .shortUri(shortUri)
                    .uv(uvCount)
                    .uip(ipCount)
                    .build();

            linkAccessStatsMapper.recordBasicAccessStats(accessStats);
        } catch (Exception ex) {
            log.info("统计数据失败", ex);
        }
    }
}
