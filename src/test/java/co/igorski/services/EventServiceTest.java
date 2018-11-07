package co.igorski.services;

import co.igorski.client.BasicHttpHttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private BasicHttpHttpClient basicHttpHttpClient;
    @Mock
    private Configuration configuration;
    @Captor
    private ArgumentCaptor<String> bodyCaptor;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void shouldSendCorrectEventStartedPost() throws IOException, SnitcherException {
        EventService eventService = new EventService(basicHttpHttpClient, configuration);

        List<TestModel> tests = new ArrayList<>();

        tests.add(new TestModel("shouldRepresentTestOne", "co.igroski.tests"));
        tests.add(new TestModel("shouldRepresentTestTwo", "co.igroski.tests"));

        String url = "http://localhost:8080/events/runStarted";
        when(configuration.getServerUrl()).thenReturn("http://localhost:8080");
        when(basicHttpHttpClient.post(eq(url), anyString())).thenReturn("{\n" +
                "  \"tests\": [\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestOne\",\n" +
                "      \"testClass\": \"co.igroski.tests\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestTwo\",\n" +
                "      \"testClass\": \"co.igroski.tests\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"started\": 1541629771671\n" +
                "}");

        TestRun testRun = eventService.testRunStarted(tests);
        verify(basicHttpHttpClient).post(eq(url), bodyCaptor.capture());
        assertThat(testRun).isNotNull();

        String bodyValue = bodyCaptor.getValue();
        TestRun capturedRun = objectMapper.readValue(bodyValue, TestRun.class);

        assertThat(capturedRun.getTests()).isEqualTo(tests);
        assertThat(capturedRun.getStarted()).isNotNull();
    }
}