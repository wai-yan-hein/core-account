/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Component
public class ActiveMQListener {

    @JmsListener(destination = "INV_MSG")
    private void receivedMessage(MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        log.info(String.format("receivedMessage: %s", entity));

    }
}
