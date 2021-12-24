/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.config;

import com.inventory.mq.MessageSubscriber;
import java.util.List;
import javax.jms.Topic;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

/**
 *
 * @author Lenovo
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableJms
public class ActiveMQConfig {

    @Autowired
    private Environment environment;
    @Autowired
    private MessageSubscriber subscriber;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(environment.getRequiredProperty("activemq.url"));
        connectionFactory.setTrustedPackages(List.of("com.inventory"));
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("INV_MSG");
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }

    @Bean
    public MessageListenerContainer messageListener() {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        // messageListenerContainer.setPubSubDomain(true);
        messageListenerContainer.setDestination(topic());
        messageListenerContainer.setMessageListener(subscriber);
        messageListenerContainer.setConnectionFactory(connectionFactory());
        log.info("messageListener config.");
        return messageListenerContainer;
    }
}
