/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.common.TokenFile;
import com.common.Util1;
import com.user.model.AuthenticationRequest;
import com.user.model.AuthenticationResponse;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

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
    private final TokenFile<AuthenticationResponse> file = new TokenFile<>(AuthenticationResponse.class);

    @Bean
    public WebClient inventoryApi() {
        String url = environment.getProperty("inventory.url");
        int port = Util1.getInteger(environment.getProperty("inventory.port"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(getUrl(url, port))
                .build();
    }

    @Bean
    public WebClient accountApi() {
        String url = environment.getProperty("account.url");
        int port = Util1.getInteger(environment.getProperty("account.port"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(getUrl(url, port))
                .build();
    }

    @Bean
    public WebClient userApi() {
        String url = environment.getProperty("user.url");
        int port = Util1.getInteger(environment.getProperty("user.port"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .defaultHeader(HttpHeaders.USER_AGENT, "Core Account")
                .baseUrl(getUrl(url, port))
                .build();
    }

    @Bean
    public String hostName() {
        return environment.getProperty("host.name");
    }

    @Bean
    public boolean localDatabase() {
        return Util1.getBoolean(environment.getProperty("local.database"));
    }

    private String getUrl(String url, int port) {
        String hostName = environment.getProperty("host.name");
        if (hostName != null) {
            String protocol = "http";
            String path = "/"; // or whatever path you want to use
            return UriComponentsBuilder.newInstance()
                    .scheme(protocol)
                    .host(Util1.getServerIp(hostName))
                    .port(port)
                    .path(path)
                    .toUriString();
        }
        return url;
    }

    private String getToken() {
        String url = environment.getProperty("user.url");
        int port = Util1.getInteger(environment.getProperty("user.port"));
        String serialNo = Util1.getBaseboardSerialNumber();
        WebClient webClient = WebClient.builder().baseUrl(getUrl(url, port)).build();
        AuthenticationResponse data = file.read();
        if (data != null) {
            if (System.currentTimeMillis() >= data.getAccessTokenExpired()) {
                log.info("token expired.");
                return authenticate(webClient, serialNo);
            } else {
                return data.getAccessToken();
            }
        }
        return authenticate(webClient, serialNo);
    }

    private String authenticate(WebClient client, String seriaNo) {
        try {
            var auth = AuthenticationRequest.builder().serialNo(seriaNo).password(seriaNo).build();
            AuthenticationResponse response = client.post()
                    .uri("/auth/authenticate")
                    .body(Mono.just(auth), AuthenticationRequest.class)
                    .retrieve()
                    .bodyToMono(AuthenticationResponse.class).block();
            if (response != null) {
                file.write(response);
                return response.getAccessToken();
            }
        } catch (Exception e) {
            log.error("authenticate : " + e.getMessage());
        }
        return null;
    }
}
