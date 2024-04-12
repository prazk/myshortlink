package com.prazk.myshortlink.project.listeners;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.mapper.*;
import com.prazk.myshortlink.project.pojo.entity.*;
import com.prazk.myshortlink.project.pojo.mq.StatsMessage;
import com.prazk.myshortlink.project.remote.resp.AmapIPLocale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.prazk.myshortlink.project.common.constant.RabbitMQConstant.LINK_STATS_DIRECT_QUEUE;

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

    @Value("${amap.region-stats.key}")
    private String amapRegionStatsKey;

    @Transactional
    @RabbitListener(queues = LINK_STATS_DIRECT_QUEUE)
    public void doStatisticsAsyn(Message message) {
        String messageId = message.getMessageProperties().getMessageId();
        // 保证业务幂等性
        LinkStatsIdempotence linkStatsIdempotence = linkStatsIdempotenceMapper.selectOne(Wrappers.lambdaQuery(LinkStatsIdempotence.class).eq(LinkStatsIdempotence::getMessageId, messageId));
        if (linkStatsIdempotence != null) {
            log.info("重复消费");
            return;
        }

        StatsMessage statsMessage = (StatsMessage) messageConverter.fromMessage(message);
        Integer uvCount = statsMessage.getUvCount();
        Long uvIncrement = statsMessage.getUvIncrement();
        String gid = statsMessage.getGid();
        String actualIP = statsMessage.getActualIP();
        String shortUri = statsMessage.getShortUri();
        String userAgent = statsMessage.getUserAgent();
        String userIdentifier = statsMessage.getUserIdentifier();

        // IP统计
        String ipKey = RedisConstant.STATS_IP_KEY_PREFIX + shortUri;

        Long ipIncrement = stringRedisTemplate.opsForHyperLogLog().add(ipKey, actualIP);
        Integer ipCount = stringRedisTemplate.opsForHyperLogLog().size(ipKey).intValue();

        // 地区统计
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

        // 操作系统统计、浏览器统计、设备类型统计
        String osName = "Unknown", browserName = "Unknown";
        int deviceType = 0;
        if (userAgent != null) {
            UserAgent agent = UserAgentUtil.parse(userAgent);
            osName = agent.getOs().getName();
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

        // PV统计
        LinkAccessStats accessStats = LinkAccessStats.builder()
                .shortUri(shortUri)
                .uv(uvCount)
                .uip(ipCount)
                .build();
        linkAccessStatsMapper.recordBasicAccessStats(accessStats);

        // 记录访问日志
        LinkAccessLogs linkAccessLogs = LinkAccessLogs.builder()
                .shortUri(shortUri)
                .user(userIdentifier)
                .browser(browserName)
                .os(osName)
                .device(deviceType)
                .ip(actualIP)
                .province(province)
                .city(city)
                .build();
        linkAccessLogsMapper.recordAccessLogs(linkAccessLogs);

        // 记录当日访问(PV UV IP)日志
        LinkStatsToday linkStatsToday = LinkStatsToday.builder()
                .shortUri(shortUri)
                .build();
        linkStatsTodayMapper.recordTodayLogs(linkStatsToday, uvIncrement, ipIncrement);

        // 记录总访问量
        linkMapper.recordAccessLogs(gid, shortUri, uvIncrement, ipIncrement);

        // 消费完毕，插入数据库
        LinkStatsIdempotence idempotence = LinkStatsIdempotence.builder().messageId(messageId).messageContent(JSONUtil.toJsonStr(statsMessage)).build();
        linkStatsIdempotenceMapper.insert(idempotence);
    }
}
