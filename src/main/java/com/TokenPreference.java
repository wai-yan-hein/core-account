package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

@Component
public class TokenPreference {

    private final Preferences preferences;
    public static String AT = "access.token";
    public static String ATE = "access.token.expired";
    public static String RT = "refresh.token";
    public static String RTE = "refresh.token.expired";

    @Autowired
    public TokenPreference(Preferences preferences) {
        this.preferences = preferences;
    }

    public void savePreference(String key, String value) {
        preferences.put(key, value);
    }

    public String getPreference(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    public boolean isTokenExpired() {
        long at = Long.parseLong(preferences.get(ATE, "0"));
        long ct = System.currentTimeMillis();
        return ct >= at;
    }
}
