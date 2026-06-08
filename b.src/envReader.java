package com.myapp;

import java.io.InputStream;
import java.util.Properties;

/*
 * custum defined class for reading,
 * usr defined enviroment related properties.
 */

public class envReader {
    private Properties props = new Properties();

    public envReader() {
        try (InputStream input=getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return props.getProperty(key);
    }

}
