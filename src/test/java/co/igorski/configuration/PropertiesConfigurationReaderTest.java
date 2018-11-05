package co.igorski.configuration;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesConfigurationReaderTest {

    @Test
    public void shouldReadInPropertiesFile() {
        PropertiesConfigurationReader propertiesConfigurationReader =
                new PropertiesConfigurationReader();
        Properties properties = propertiesConfigurationReader.readProperties("configuration.properties");

        assertThat(properties.values().size()).isEqualTo(2);
        assertThat(properties.getProperty("username")).isEqualTo("testUser");
        assertThat(properties.getProperty("password")).isEqualTo("testPassword");
    }

}