package co.igorski.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfigurationReader implements ConfigurationReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurationReader.class);

    @Override
    public Properties readProperties(String resourceLocation) {
        var properties = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceLocation)) {
            properties.load(in);
        } catch (IOException | NullPointerException e) {
            LOGGER.warn("Failed to read client properties file!", e);
        }

        return properties;
    }
}
