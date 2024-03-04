/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Lenovo
 */
@Slf4j
@Configuration
@Conditional(H2DBCondition.class)
@EnableTransactionManagement
@PropertySource("classpath:application.properties") // Use the same properties file as your main configuration
public class H2DBConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:./data/database");
        config.setUsername("root");
        config.setPassword("corevalue");
        config.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPackagesToScan("com.user.model", "com.inventory.entity", "com.acc.model");
        em.setJpaProperties(additionalJpaProperties());
        return em;
    }

    private Properties additionalJpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update"); // Example JPA property
        /* properties.setProperty("hibernate.connection.username", "root");
        properties.setProperty("hibernate.connection.password", "corevalue");
        properties.setProperty("hibernate.connection.url", "jdbc:h2:file:./data/database");*/
        properties.setProperty("spring.h2.console.enabled", "true");
        properties.setProperty("spring.h2.console.path", "/h2-console");
        //properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform");
        return properties;
    }
}
