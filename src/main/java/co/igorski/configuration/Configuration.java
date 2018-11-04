package co.igorski.configuration;

import java.util.Properties;

public class Configuration {
    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public String getServerUrl() {
        return properties.getProperty("serverUrl");
    }

    public String getUsername() {
        return properties.getProperty("username");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }
}
