package com.prazk.myshortlink.project.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.biz.common.constant.CommonConstant;
import com.prazk.myshortlink.project.biz.common.constant.LinkConstant;
import com.prazk.myshortlink.project.biz.common.constant.RabbitMQConstant;
import com.prazk.myshortlink.project.biz.common.constant.RedisConstant;
import com.prazk.myshortlink.project.biz.common.enums.ValidDateTypeEnum;
import com.prazk.myshortlink.project.biz.mapper.LinkGotoMapper;
import com.prazk.myshortlink.project.biz.mapper.LinkMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.Link;
import com.prazk.myshortlink.project.biz.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.biz.pojo.mq.StatsMessage;
import com.prazk.myshortlink.project.biz.service.LinkGotoService;
import com.prazk.myshortlink.project.biz.util.LinkUtil;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGoto> implements LinkGotoService {

    private final RBloomFilter<String> shortLinkGenerationBloomFilter;
    private final LinkMapper linkMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${project.link.goto.tryLock.time}")
    private long time;

    @SneakyThrows
    @Override
    public void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response) {
        // 先查询布隆过滤器，再查询数据库
        String shortUri = linkRestoreDTO.getShortUri();
        // 参数合法性检查
        if (shortUri.length() != LinkConstant.LINK_LENGTH) {
            log.info("短链接参数不合法");
            response.sendRedirect("/link/notfound");
            return;
        }
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
        String originUrl = stringRedisTemplate.opsForValue().get(key);
        if (!StrUtil.isBlank(originUrl)) { // 不为null且不为空
            log.info("缓存命中");
            // 统计访问数据
            doStatistics(request, response, shortUri);
            // 重定向结果
            response.sendRedirect(originUrl);
            return;
        }
        if ("".equals(originUrl)) {
            log.info("查询到空数据");
            response.sendRedirect("/link/notfound");
            return;
        }
        // 查询到null，缓存未命中，访问数据库
        log.info("缓存未命中");
        // 存在缓存击穿问题：一个热点key失效，同时大量请求访问这个key，导致大量请求访问到数据库
        // 分布式锁解决缓存击穿问题，锁对象是短链接
        RLock lock = redissonClient.getLock(RedisConstant.LOCK_GOTO_SHORT_LINK_KEY_PREFIX + shortUri);
        // 使用 lock 还是 tryLock
        // 1. 如果使用tryLock，是否需要设置waitTime？
        //    如果不使用双重判定锁，则不设置，因为只需要有一个线程进行缓存重建，其他线程没有必有再等待一个线程重建完毕后再次进行缓存重建
        //    如果配合双重判断锁使用，可以设置，使用双重判断锁后其他线程走的是缓存
        // 2. 这里使用tryLock，且不设置waitTime的缺点：没有获取锁的线程就直接返回错误，用户体验较差，高并发场景下只有一个用户可以正常跳转，
        //    其他用户则需要再刷新一次，如果重建时间长，甚至需要刷新多次；而优点是：可以快速返回失败，不用阻塞线程
        // 3. 如果使用lock存在的问题是：发生缓存击穿时，会有很多线程阻塞等待获取锁，并且获取锁后还是要访问数据库
        //    可以使用lock配合双重判定锁，让其他线程走缓存
        // 综上，有三种方案选择：
        // 1. 使用tryLock，不设置waitTime：获取不到锁直接返回失败，用户体验不好，甚至可能需要刷新很多次
        // 2. 使用tryLock，设置waitTime，并使用双重判断锁：可以控制获取锁的时间，超过这个时间就返回失败
        // 3. 使用lock，并使用双重判断锁，实际上就是方案二的waitTime设为无穷大：理想情况下一定会返回结果，但是用户等久了还是体验不好
        // 这里选择折中的方案二，当出现缓存重建时间长的情况时，既不会让用户频繁地刷新而得到的还是404 NOT FOUND，也不会出现浏览器长时间没有被响应的情况
        if (lock.tryLock(time, TimeUnit.MILLISECONDS)) {
            try {
                // 判断缓存是否重建完成，如果已经重建，则无需再访问数据库
                originUrl = stringRedisTemplate.opsForValue().get(key);
                if (!StrUtil.isBlank(originUrl)) { // 不为null且不为空
                    log.info("缓存命中");
                    // 统计访问数据
                    doStatistics(request, response, shortUri);
                    // 重定向结果
                    response.sendRedirect(originUrl);
                    return;
                }
                if ("".equals(originUrl)) {
                    log.info("查询到空数据");
                    response.sendRedirect("/link/notfound");
                    return;
                }

//                Thread.sleep(10000); // 模拟重建时间长的场景
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
                QueryWrapper<Link> linkWrapper = new QueryWrapper<>();
                linkWrapper.select("origin_uri as originUrl", "valid_date as validDate", "valid_date_type as validDateType")
                        .lambda()
                        .eq(Link::getGid, gid)
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
                    // 为了避免缓存击穿，对于过期的情况也需要缓存空值
                    stringRedisTemplate.opsForValue().set(key, "", RedisConstant.GOTO_SHORT_LINK_EMPTY_VALUE_DURATION);
                    throw new ClientException(BaseErrorCode.LINK_EXPIRED_ERROR);
                }
                stringRedisTemplate.opsForValue().set(key, link.getOriginUrl(), expire);
                // 统计访问数据
                doStatistics(request, response, shortUri);
                response.sendRedirect(link.getOriginUrl());
            } finally {
                lock.unlock();
            }
        } else {
            throw new ClientException(BaseErrorCode.SERVICE_BUSY_ERROR);
        }
    }

    /**
     * 缓存或数据库命中时进行短链接访问统计
     */
    private void doStatistics(HttpServletRequest request, HttpServletResponse response, String shortUri) {
        try {
            // 获取gid
            LambdaQueryWrapper<LinkGoto> linkGotoWrapper = new LambdaQueryWrapper<>();
            linkGotoWrapper.eq(LinkGoto::getShortUri, shortUri);
            LinkGoto linkGoto = getOne(linkGotoWrapper);
            String gid = linkGoto.getGid();

            // UV统计
            String uvKey = RedisConstant.STATS_UV_KEY_PREFIX + shortUri;
            Cookie[] cookies = request.getCookies();
            String userIdentifier = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("uv")) {
                        userIdentifier = cookie.getValue();
                        break;
                    }
                }
            }

            if (userIdentifier == null) {
                userIdentifier = IdUtil.fastSimpleUUID();
                Cookie uv = new Cookie("uv", userIdentifier);
                uv.setMaxAge(3600 * 24 * 30);
                // 生成的Cookie是用户的唯一标识，即使被不同短链接路径共享，也没关系
                uv.setPath("/");
                response.addCookie(uv);
            }

            Long uvIncrement = stringRedisTemplate.opsForHyperLogLog().add(uvKey, userIdentifier);
            Integer uvCount = stringRedisTemplate.opsForHyperLogLog().size(uvKey).intValue();

            // 发送异步统计数据
            StatsMessage statsMessage = StatsMessage.builder()
                    .uvCount(uvCount)
                    .gid(gid)
                    .actualIP(LinkUtil.getActualIP(request))
                    .userIdentifier(userIdentifier)
                    .userAgent(request.getHeader("User-Agent"))
                    .uvIncrement(uvIncrement)
                    .shortUri(shortUri)
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstant.LINK_STATS_FANOUT_EXCHANGE, null, statsMessage);

        } catch (Exception ex) {
            log.error("统计数据失败", ex);
        }
    }
}
