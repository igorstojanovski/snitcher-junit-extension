package co.igorski.services;

import co.igorski.client.HttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.User;
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

    User login() {
        User user = null;
        Map<String, String> form = new HashMap<>();
        form.put("username", configuration.getUsername());
        form.put("password", configuration.getPassword());

        try {
            int responseStatus = httpClient.postForm(configuration.getServerUrl(), form);

            if(responseStatus == 200) {
                user = new User();
                user.setUsername(configuration.getUsername());
            }

        } catch (IOException | SnitcherException e) {
            LOGGER.error("Error while trying to log in.", e);
        }

        return user;
    }
}
