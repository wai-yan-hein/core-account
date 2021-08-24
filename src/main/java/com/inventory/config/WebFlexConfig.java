/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Configuration
@Slf4j
public class WebFlexConfig {
    
    @Autowired
    private Environment environment;
    
    @Bean
    public WebClient webClient() {
        log.info("webClient : " + environment.getProperty("base.url"));
        return WebClient.create(environment.getProperty("base.url"));
    }
}
