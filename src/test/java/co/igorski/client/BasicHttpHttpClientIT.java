package co.igorski.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import extensions.Wiremock;
import extensions.WiremockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WiremockExtension.class)
class BasicHttpHttpClientIT {

    @Test
    public void shouldPostForm(@Wiremock WireMockServer server) throws IOException {

        server.stubFor(post(urlEqualTo("/login"))
                .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                .withRequestBody(equalTo("password=theusualpassword&username=kaysersoze"))
                .willReturn(ok()));

        HttpClient client = new BasicHttpHttpClient();
        Map<String, String> form = new HashMap<>();
        form.put("username", "kaysersoze");
        form.put("password", "theusualpassword");

        int statusCode = client.postForm("http://localhost:" + server.port() + "/login", form);

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void shouldPost(@Wiremock WireMockServer server) throws IOException {

        String body = "{}";

        server.stubFor(post(urlEqualTo("/postTest"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(body))
                .willReturn(aResponse().withBody("something")));

        HttpClient client = new BasicHttpHttpClient();
        String response = client.post("http://localhost:" + server.port() + "/postTest", body);

        assertThat(response).isEqualTo("something");
    }

    @Test
    public void shouldGet(@Wiremock WireMockServer server) throws IOException {

        String body = "{}";

        server.stubFor(get(urlEqualTo("/postGet"))
                .withRequestBody(equalTo(body))
                .willReturn(aResponse().withBody("something")));

        HttpClient client = new BasicHttpHttpClient();
        String response = client.post("http://localhost:" + server.port() + "/postGet", body);

        assertThat(response).isEqualTo("something");
    }
}