package com.prazk.myshortlink.project.biz.common.constant;

public class RabbitMQConstant {
    /**
     * 短链接监控数据统计fanout广播交换机
     */
    public static final String LINK_STATS_FANOUT_EXCHANGE = "short-link.stats.fanout";
    /**
     * 短链接监控数据统计队列
     */
    public static final String LINK_STATS_DIRECT_QUEUE = "stats.direct.queue";
    /**
     * 短链接监控数据统计，消费者消息业务连续出错时，人工干预队列
     */
    public static final String LINK_STATS_ERROR_QUEUE = "stats.error.queue";
    /**
     * 短链接监控数据统计，消费者消息业务连续出错时，人工干预交换机
     */
    public static final String LINK_STATS_ERROR_EXCHANGE = "short-link.stats.error.direct";
    /**
     * 统计数据业务异常时，交换机与人工处理消息的队列绑定的key
     */
    public static final String LINK_STATS_ERROR_ROUTING_KEY = "stats.error";
}
