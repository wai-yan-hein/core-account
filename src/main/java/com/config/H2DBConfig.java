/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.config;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author Lenovo
 */
@Configuration
@Conditional(H2DBCondition.class)
@PropertySource("classpath:application.properties") // Use the same properties file as your main configuration
public class H2DBConfig {

}
