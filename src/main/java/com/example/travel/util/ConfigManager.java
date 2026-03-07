package com.example.travel.util;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.travelapp/";
    private static final String CONFIG_FILE = CONFIG_DIR + "config.properties";
    private Properties props = new Properties();

    public ConfigManager() {
        File file = new File(CONFIG_FILE);
        // Создаём директорию, если её нет
        file.getParentFile().mkdirs();
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        File file = new File(CONFIG_FILE);
        // Родительская директория уже должна существовать (создана в конструкторе)
        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Travel App Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    public long getUserId() {
        return Long.parseLong(get("user.id", "0"));
    }

    public void setUserId(long id) {
        set("user.id", String.valueOf(id));
    }

    public double getWindowWidth() {
        return Double.parseDouble(get("window.width", "800"));
    }

    public void setWindowWidth(double width) {
        set("window.width", String.valueOf(width));
    }

    public double getWindowHeight() {
        return Double.parseDouble(get("window.height", "800"));
    }

    public void setWindowHeight(double height) {
        set("window.height", String.valueOf(height));
    }
}