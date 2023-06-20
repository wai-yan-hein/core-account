package com.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

@Component
public class MyComponent {

    private final Preferences preferences;

    @Autowired
    public MyComponent(Preferences preferences) {
        this.preferences = preferences;
    }

    public void savePreference(String key, String value) {
        preferences.put(key, value);
    }

    public String getPreference(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }
}
