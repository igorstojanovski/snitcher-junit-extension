package co.igorski.services;

import co.igorski.client.HttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
    @Mock
    private Configuration configuration;
    @Mock
    private HttpClient httpClient;
    private HashMap<String, String> form;
    private static final String USERNAME = "keysersoze";
    private static final String PASSWORD = "theusualpassword";

    @BeforeEach
    public void beforeEach() {
        Mockito.when(configuration.getServerUrl()).thenReturn(HTTP_LOCALHOST_8080);
        Mockito.when(configuration.getUsername()).thenReturn(USERNAME);
        Mockito.when(configuration.getPassword()).thenReturn(PASSWORD);

        form = new HashMap<>();
        form.put("username", USERNAME);
        form.put("password", PASSWORD);
    }

    @Test
    public void shouldReturnTrueWhenLoginSucceeded() throws IOException {
        LoginService loginService = new LoginService(configuration, httpClient);

        when(httpClient.postForm(HTTP_LOCALHOST_8080, form)).thenReturn(200);

        User user = loginService.login();

        verify(httpClient).postForm(HTTP_LOCALHOST_8080, form);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void shouldReturnFalseWhenLoginFailed() throws IOException {
        LoginService loginService = new LoginService(configuration, httpClient);

        when(httpClient.postForm(HTTP_LOCALHOST_8080, form)).thenReturn(401);

        User user = loginService.login();

        verify(httpClient).postForm(HTTP_LOCALHOST_8080, form);
        assertThat(user).isNull();
    }
}