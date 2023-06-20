package com.config;
import java.util.prefs.Preferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PreferencesConfig {

    @Bean
    public Preferences preferences() {
        return Preferences.userRoot().node("tokenPreference");
    }
}
