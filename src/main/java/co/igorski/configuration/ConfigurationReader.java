package co.igorski.configuration;

import java.util.Properties;

/**
 * Implementations read in configuration from different source types.
 */
public interface ConfigurationReader {

    /**
     * Reads configuration properties from the given location.
     *
     * @param resourceLocation relative to the resource folder
     * @return object containing all the configuration properties
     */
    Properties readProperties(String resourceLocation);

}
