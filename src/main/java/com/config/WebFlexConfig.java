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
import java.awt.HeadlessException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

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
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .baseUrl(getUrl(url, port))
                .clientConnector(reactorClientHttpConnector())
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
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .baseUrl(getUrl(url, port))
                .clientConnector(reactorClientHttpConnector())
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
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .baseUrl(getUrl(url, port))
                .clientConnector(reactorClientHttpConnector())
                .build();
    }

    @Bean
    public WebClient hmsApi() {
        String url = environment.getProperty("hms.url");
        int port = Util1.getInteger(environment.getProperty("hms.port"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
                .baseUrl(getUrl(url, port))
                .clientConnector(reactorClientHttpConnector())
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

    @Bean
    public String getToken() {
        log.info("getToken.");
        String url = environment.getProperty("user.url");
        int port = Util1.getInteger(environment.getProperty("user.port"));
        String serialNo = Util1.getBaseboardSerialNumber();
        WebClient webClient = WebClient.builder().baseUrl(getUrl(url, port)).build();
        return authenticate(webClient, serialNo).block();
    }

    private Mono<String> authenticate(WebClient client, String serialNo) {
        var auth = AuthenticationRequest.builder()
                .serialNo(serialNo)
                .password(Util1.getPassword())
                .build();

        return client.post()
                .uri("/auth/authenticate")
                .body(Mono.just(auth), AuthenticationRequest.class)
                .retrieve()
                .bodyToMono(AuthenticationResponse.class)
                .flatMap(response -> {
                    if (response != null && response.getAccessToken() != null) {
                        try {
                            file.write(response); // Assuming this writes the response to a file
                            log.info("New token: " + response.getAccessToken());
                        } catch (Exception e) {
                            log.error("Error writing response to file: " + e.getMessage());
                        }
                        return Mono.just(response.getAccessToken());
                    } else {
                        return Mono.just("");
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error during authentication: " + e.getMessage());
                    return Mono.just(file.read().getAccessToken()); // Return an empty Mono in case of error
                });
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("custom-provider")
                .maxConnections(10) // maximum number of connections
                .maxIdleTime(Duration.ofSeconds(10)) // maximum idle time
                .maxLifeTime(Duration.ofSeconds(60)) // maximum life time
                .pendingAcquireTimeout(Duration.ofSeconds(5)) // pending acquire timeout
                .evictInBackground(Duration.ofSeconds(30)) // eviction interval
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create(connectionProvider());
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector() {
        return new ReactorClientHttpConnector(httpClient());
    }
}
