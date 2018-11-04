package co.igorski.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {

    private static final String HTTP_LOCALHOST_8181 = "http://localhost:8181";

    @Test
    public void shouldReturnTestUrl() {
        Properties properties = new Properties();
        properties.put("serverUrl", HTTP_LOCALHOST_8181);
        Configuration configuration = new Configuration(properties);

        assertThat(configuration.getServerUrl()).isEqualTo("http://localhost:8181");
    }

}