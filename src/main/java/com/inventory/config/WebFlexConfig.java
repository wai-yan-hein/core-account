/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.config;

import com.common.Util1;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Configuration
@Slf4j
@PropertySource(value = {"file:config/application.properties"})
public class WebFlexConfig {

    @Autowired
    private Environment environment;

    @Bean
    public WebClient inventoryApi() {
        String url = environment.getProperty("inventory.url");
        String hostName = environment.getProperty("host.name");
        if (hostName != null) {
            int port = Util1.getInteger(environment.getProperty("inventory.port"));
            String protocol = "http";
            String path = "/"; // or whatever path you want to use
            try {
                url = new URL(protocol, Util1.getServerIp(hostName), port, path).toString();
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
                System.exit(0);
            }
        }
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient accountApi() {
        String url = environment.getProperty("account.url");
        String hostName = environment.getProperty("host.name");
        if (hostName != null) {
            int port = Util1.getInteger(environment.getProperty("account.port"));
            String protocol = "http";
            String path = "/"; // or whatever path you want to use
            try {
                url = new URL(protocol, Util1.getServerIp(hostName), port, path).toString();
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
                System.exit(0);
            }
        }
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(url)
                .build();
    }

    @Bean
    public WebClient userApi() {
        String url = environment.getProperty("user.url");
        String hostName = environment.getProperty("host.name");
        if (hostName != null) {
            int port = Util1.getInteger(environment.getProperty("user.port"));
            String protocol = "http";
            String path = "/"; // or whatever path you want to use
            try {
                url = new URL(protocol, Util1.getServerIp(hostName), port, path).toString();
            } catch (MalformedURLException ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage());
                System.exit(0);
            }
        }
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(url)
                .build();
    }
}
