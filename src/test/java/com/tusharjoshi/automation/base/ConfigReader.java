package com.tusharjoshi.automation.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        String env = System.getProperty("env", "qa");          // default: qa
        String file = "config/" + env + ".properties";

        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new RuntimeException("Config file not found: " + file);
            }
            props.load(in);
            System.out.println("Loaded config: " + file);
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + file, e);
        }
    }

    public static String get(String key) {
        String value = System.getProperty(key, props.getProperty(key));   // command line wins
        if (value == null) {
            throw new RuntimeException("Missing config key: " + key);
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}