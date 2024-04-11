package com.prazk.myshortlink.project.common.config;

import com.prazk.myshortlink.project.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
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

}
