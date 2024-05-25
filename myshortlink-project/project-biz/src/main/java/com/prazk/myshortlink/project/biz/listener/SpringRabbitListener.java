package com.prazk.myshortlink.project.biz.listener;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.prazk.myshortlink.project.biz.common.constant.RabbitMQConstant;
import com.prazk.myshortlink.project.biz.common.constant.RedisConstant;
import com.prazk.myshortlink.project.biz.mapper.*;
import com.prazk.myshortlink.project.biz.pojo.entity.*;
import com.prazk.myshortlink.project.biz.pojo.mq.StatsMessage;
import com.prazk.myshortlink.project.biz.pojo.resp.AmapIPLocale;
import com.prazk.myshortlink.project.biz.util.StatsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
@Slf4j
public class SpringRabbitListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final LinkMapper linkMapper;
    private final MessageConverter messageConverter;
    private final LinkStatsIdempotenceMapper linkStatsIdempotenceMapper;
    private final RedissonClient redissonClient;

    @Value("${amap.region-stats.key}")
    private String amapRegionStatsKey;

    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(
            20,
            20,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000));

    @Transactional
    @RabbitListener(queues = RabbitMQConstant.LINK_STATS_DIRECT_QUEUE)
    public void doStatisticsAsyn(Message message) {
        StatsMessage statsMessage = (StatsMessage) messageConverter.fromMessage(message);
        String messageId = message.getMessageProperties().getMessageId();

        String lockKey = RedisConstant.LOCK_STATS_ASYN_KEY_PREFIX + messageId;
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                Long uvIncrement = statsMessage.getUvIncrement();
                String gid = statsMessage.getGid();
                String actualIP = statsMessage.getActualIP();
                String shortUri = statsMessage.getShortUri();
                String userAgent = statsMessage.getUserAgent();
                String userIdentifier = statsMessage.getUserIdentifier();

                // 保证业务幂等性
                LinkStatsIdempotence linkStatsIdempotence = linkStatsIdempotenceMapper
                        .selectOne(Wrappers.lambdaQuery(LinkStatsIdempotence.class)
                                .eq(LinkStatsIdempotence::getMessageId, messageId));
                if (linkStatsIdempotence != null) {
                    log.info("重复消费");
                    return;
                }

                // 地区统计
                Future<LocaleStats> localeStatsFuture = POOL_EXECUTOR.submit(() -> getLocaleStats(actualIP, shortUri));

                // IP统计
                Future<IpStats> ipStatsFuture = POOL_EXECUTOR.submit(() -> getIpStats(shortUri, actualIP));

                // OS、浏览器、设备统计
                Future<OsBrowserDeviceStats> osBrowserDeviceStatsFuture =
                        POOL_EXECUTOR.submit(() -> getOsBrowserDeviceStats(userAgent, shortUri));

                // 记录访问日志
                Future<?> recordOk1 = POOL_EXECUTOR.submit(() -> {
                    try {
                        OsBrowserDeviceStats osBrowserDeviceStats = osBrowserDeviceStatsFuture.get();
                        LocaleStats locale = localeStatsFuture.get();
                        recordAccessLogs(shortUri, userIdentifier, osBrowserDeviceStats, actualIP, locale);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

                Future<?> recordOk2 = POOL_EXECUTOR.submit(() -> {
                    try {
                        IpStats ipStats = ipStatsFuture.get();
                        Future<?> submit1 = POOL_EXECUTOR.submit(() -> {
                            // 记录基本访问数据
                            recordBasicAccessStats(shortUri, uvIncrement.intValue(), ipStats.ipIncrement.intValue());
                        });
                        Future<?> submit2 = POOL_EXECUTOR.submit(() -> {
                            // 记录当日访问(PV UV IP)日志
                            recordAccessToday(shortUri, uvIncrement, ipStats);
                        });
                        Future<?> submit3 = POOL_EXECUTOR.submit(() -> {
                            // 记录总访问量
                            recordAccessLogs(gid, shortUri, uvIncrement, ipStats);
                        });
                        submit1.get();
                        submit2.get();
                        submit3.get();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

                try {
                    recordOk1.get();
                    recordOk2.get();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

                // 消费完毕，插入数据库
                InsertMessageLog(messageId, statsMessage);

            } catch (Throwable e) {
                log.error("短链接统计业务异常");
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }

    private void recordAccessLogs(String gid, String shortUri, Long uvIncrement, IpStats IpStats) {
        linkMapper.recordAccessLogs(gid, shortUri, uvIncrement, IpStats.ipIncrement());
    }

    private void InsertMessageLog(String messageId, StatsMessage statsMessage) {
        LinkStatsIdempotence idempotence = LinkStatsIdempotence
                .builder()
                .messageId(messageId)
                .messageContent(JSONUtil.toJsonStr(statsMessage))
                .build();
        linkStatsIdempotenceMapper.insert(idempotence);
    }

    private void recordAccessToday(String shortUri, Long uvIncrement, IpStats IpStats) {
        LinkStatsToday linkStatsToday = LinkStatsToday.builder()
                .shortUri(shortUri)
                .build();
        linkStatsTodayMapper.recordTodayLogs(linkStatsToday, uvIncrement, IpStats.ipIncrement());
    }

    private void recordBasicAccessStats(String shortUri, Integer uvIncrement, Integer ipIncrement) {
        LinkAccessStats accessStats = LinkAccessStats.builder()
                .shortUri(shortUri)
                .uv(uvIncrement)
                .uip(ipIncrement)
                .build();
        linkAccessStatsMapper.recordBasicAccessStats(accessStats);
    }

    /**
     * ip统计
     */
    private IpStats getIpStats(String shortUri, String actualIP) {
        String ipKey = RedisConstant.STATS_IP_KEY_PREFIX + shortUri;
        Long ipIncrement = stringRedisTemplate.opsForHyperLogLog().add(ipKey, actualIP);
        Integer ipCount = stringRedisTemplate.opsForHyperLogLog().size(ipKey).intValue();
        return new IpStats(ipIncrement, ipCount);
    }

    private record IpStats(Long ipIncrement, Integer ipCount) {
    }

    /**
     * 记录访问日志
     */
    private void recordAccessLogs(String shortUri, String userIdentifier, OsBrowserDeviceStats osBrowserDeviceStats, String actualIP, LocaleStats locale) {
        // 记录访问日志
        LinkAccessLogs linkAccessLogs = LinkAccessLogs.builder()
                .shortUri(shortUri)
                .user(userIdentifier)
                .browser(osBrowserDeviceStats.browserName())
                .os(osBrowserDeviceStats.osName())
                .device(osBrowserDeviceStats.deviceType())
                .ip(actualIP)
                .province(locale.province())
                .city(locale.city())
                .build();
        linkAccessLogsMapper.recordAccessLogs(linkAccessLogs);
    }

    /**
     * 操作系统统计、浏览器统计、设备类型统计
     */
    private OsBrowserDeviceStats getOsBrowserDeviceStats(String userAgent, String shortUri) {
        String osName = "Unknown", browserName = "Unknown";
        int deviceType = 0;
        if (userAgent != null) {
            UserAgent agent = UserAgentUtil.parse(userAgent);
            osName = StatsUtil.dealOsName(agent.getOs().getName());
            browserName = agent.getBrowser().getName();
            deviceType = agent.isMobile() ? 1 : 0; // 设备种类 0桌面端 1移动端

            LinkOsStats osStats = LinkOsStats.builder()
                    .os(osName)
                    .shortUri(shortUri)
                    .build();
            linkOsStatsMapper.recordOsAccessStats(osStats);

            LinkBrowserStats linkBrowserStats = LinkBrowserStats.builder()
                    .browser(browserName)
                    .shortUri(shortUri)
                    .build();
            linkBrowserStatsMapper.recordBrowserAccessStats(linkBrowserStats);

            LinkDeviceStats linkDeviceStats = LinkDeviceStats.builder()
                    .device(deviceType)
                    .shortUri(shortUri)
                    .build();
            linkDeviceStatsMapper.recordDeviceAccessStats(linkDeviceStats);
        }
        OsBrowserDeviceStats osBrowserDeviceStats = new OsBrowserDeviceStats(osName, browserName, deviceType);
        return osBrowserDeviceStats;
    }

    private record OsBrowserDeviceStats(String osName, String browserName, int deviceType) {
    }

    /**
     * 地区统计
     */
    private LocaleStats getLocaleStats(String actualIP, String shortUri) {
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("key", amapRegionStatsKey);
        reqParams.put("ip", actualIP);
        String respBody = HttpUtil.get("https://restapi.amap.com/v3/ip", reqParams);
        AmapIPLocale amapIPLocale = JSONUtil.toBean(respBody, AmapIPLocale.class);
        String city = "未知", province = "未知";
        if (amapIPLocale.getInfocode().equals("10000")) {
            city = amapIPLocale.getCity().equals("[]") ? "未知" : amapIPLocale.getCity();
            String adcode = amapIPLocale.getAdcode().equals("[]") ? "未知" : amapIPLocale.getAdcode();
            province = amapIPLocale.getProvince().equals("[]") ? "未知" : amapIPLocale.getProvince();

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
        return new LocaleStats(city, province);
    }

    private record LocaleStats(String city, String province) {
    }
}
