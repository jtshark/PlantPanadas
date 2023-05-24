package de.plant.pandas;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Keys {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("keys.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.out.println("Could not find 'keys.properties' file in the classpath.");
            }
        } catch (IOException e) {
            System.out.println("Failed to load 'keys.properties' file from the classpath.");
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}