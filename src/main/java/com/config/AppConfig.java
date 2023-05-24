/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.common.ui.ApplicationMainFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Lenovo
 */
@Configuration
public class AppConfig {

    @Bean
    public ApplicationMainFrame applicationMainFrame() {
        return new ApplicationMainFrame();
    }
}
