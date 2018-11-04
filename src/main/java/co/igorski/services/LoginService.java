package co.igorski.services;

import co.igorski.client.HttpClient;
import co.igorski.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private final Configuration configuration;
    private final HttpClient httpClient;

    LoginService(Configuration configuration, HttpClient httpClient) {
        this.configuration = configuration;
        this.httpClient = httpClient;
    }

    boolean login() {
        boolean isLoggedIn = false;
        Map<String, String> form = new HashMap<>();
        form.put("username", configuration.getUsername());
        form.put("password", configuration.getPassword());

        try {
            int responseStatus = httpClient.postForm(configuration.getServerUrl(), form);

            if(responseStatus == 200) {
                isLoggedIn = true;
            }

        } catch (IOException e) {
            LOGGER.error("Error while trying to log in.", e);
        }

        return isLoggedIn;
    }
}
