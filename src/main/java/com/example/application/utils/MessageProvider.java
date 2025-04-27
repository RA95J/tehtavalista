package com.example.application.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {

    private static Locale currentLocale = Locale.getDefault();

    public static String get(String key) {
        return get(currentLocale, key);
    }

    public static String get(Locale locale, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }
}
