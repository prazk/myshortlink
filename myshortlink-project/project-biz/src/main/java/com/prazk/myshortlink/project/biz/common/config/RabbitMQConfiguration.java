package com.prazk.myshortlink.project.biz.common.config;

import com.prazk.myshortlink.project.biz.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfiguration {
    @Bean
    public MessageConverter messageConverter(){
        Jackson2JsonMessageConverter jjmc = new Jackson2JsonMessageConverter();
        jjmc.setCreateMessageIds(true);
        return jjmc;
    }

    @Bean
    public FanoutExchange linkStatsFanoutExchange() {
        return new FanoutExchange(RabbitMQConstant.LINK_STATS_FANOUT_EXCHANGE);
    }

    @Bean
    public Queue linkStatsDirectQueue() {
        return new Queue(RabbitMQConstant.LINK_STATS_DIRECT_QUEUE);
    }

    @Bean
    public Binding bindingQueue(Queue linkStatsDirectQueue, FanoutExchange linkStatsFanoutExchange){
        return BindingBuilder.bind(linkStatsDirectQueue).to(linkStatsFanoutExchange);
    }

    @Bean
    public DirectExchange linkStatsErrorExchange(){
        return new DirectExchange(RabbitMQConstant.LINK_STATS_ERROR_EXCHANGE);
    }

    @Bean
    public Queue linkStatsErrorQueue(){
        return new Queue(RabbitMQConstant.LINK_STATS_ERROR_QUEUE, true);
    }

    @Bean
    public Binding errorBinding(Queue linkStatsErrorQueue, DirectExchange linkStatsErrorExchange){
        return BindingBuilder.bind(linkStatsErrorQueue).to(linkStatsErrorExchange).with(RabbitMQConstant.LINK_STATS_ERROR_ROUTING_KEY);
    }

    @Bean
    public MessageRecoverer statsRepublishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, RabbitMQConstant.LINK_STATS_ERROR_EXCHANGE, RabbitMQConstant.LINK_STATS_ERROR_ROUTING_KEY);
    }

}
