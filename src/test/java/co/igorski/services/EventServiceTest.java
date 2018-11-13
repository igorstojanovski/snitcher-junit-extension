package co.igorski.services;

import co.igorski.client.BasicHttpHttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import co.igorski.model.events.RunFinished;
import co.igorski.model.events.RunStarted;
import co.igorski.model.events.TestFinished;
import co.igorski.model.events.TestStarted;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    public static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
    public static final String TEST_CLASS = "co.igorski.tests.EventServiceTest";
    @Mock
    private BasicHttpHttpClient basicHttpHttpClient;
    @Mock
    private Configuration configuration;
    @Captor
    private ArgumentCaptor<String> bodyCaptor;
    private ObjectMapper objectMapper = new ObjectMapper();
    public static final Long RUN_ID = 1L;

    @BeforeEach
    public void beforeEach() {
        when(configuration.getServerUrl()).thenReturn(HTTP_LOCALHOST_8080);
    }

    @Test
    public void shouldSendCorrectEventStartedPost() throws IOException, SnitcherException {
        EventService eventService = new EventService(basicHttpHttpClient, configuration);

        Map<String, TestModel> tests = new HashMap<>();

        TestModel one = new TestModel();
        one.setTestClass(TEST_CLASS);
        one.setTestName("shouldRepresentTestOne");

        TestModel two = new TestModel();
        two.setTestClass(TEST_CLASS);
        two.setTestName("shouldRepresentTestTwo");

        tests.put(one.uniqueId(), one);
        tests.put(two.uniqueId(), two);

        String url = HTTP_LOCALHOST_8080 + "/events/runStarted";
        when(basicHttpHttpClient.post(eq(url), anyString())).thenReturn("{\n" +
                "  \"id\": 1,\n" +
                "  \"tests\": [\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestOne\",\n" +
                "      \"testClass\": \"co.igorski.tests.EventServiceTest\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestTwo\",\n" +
                "      \"testClass\": \"co.igorski.tests.EventServiceTest\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"startTime\": 1541629771671\n" +
                "}");

        User user = new User();
        user.setUsername("someUser");

        TestRun startedTestRun = eventService.testRunStarted(tests, user);

        verify(basicHttpHttpClient).post(eq(url), bodyCaptor.capture());
        assertThat(startedTestRun).isNotNull();
        assertThat(startedTestRun.getId()).isEqualTo(RUN_ID);

        String bodyValue = bodyCaptor.getValue();
        RunStarted runStarted = objectMapper.readValue(bodyValue, RunStarted.class);

        assertThat(runStarted.getTests()).isEqualTo(new ArrayList<>(tests.values()));
        assertThat(runStarted.getTimestamp()).isNotNull();
        assertThat(runStarted.getUser().getUsername()).isEqualTo("someUser");

    }

    @Test
    public void shouldSendCorrectEventFinishedPost() throws IOException, SnitcherException {
        EventService eventService = new EventService(basicHttpHttpClient, configuration);

        String url = HTTP_LOCALHOST_8080 + "/events/runFinished";
        when(basicHttpHttpClient.post(eq(url), anyString())).thenReturn("{\n" +
                "  \"id\": 1,\n" +
                "  \"tests\": [\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestOne\",\n" +
                "      \"testClass\": \"co.igorski.tests.EventServiceTest\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"testName\": \"shouldRepresentTestTwo\",\n" +
                "      \"testClass\": \"co.igorski.tests.EventServiceTest\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"startTime\": 1541629771671,\n" +
                "  \"endTime\": 1541629779781\n" +
                "}");

        TestRun testRun = eventService.testRunFinished(RUN_ID);
        assertThat(testRun).isNotNull();
        assertThat(testRun.getId()).isEqualTo(RUN_ID);

        verify(basicHttpHttpClient).post(eq(url), bodyCaptor.capture());
        RunFinished runFinished = objectMapper.readValue(bodyCaptor.getValue(), RunFinished.class);
        assertThat(runFinished.getRunId()).isNotNull();
        assertThat(runFinished.getTimestamp()).isNotNull();
    }

    @Test
    public void shouldSendTestStartedEvent() throws IOException, SnitcherException {
        EventService eventService = new EventService(basicHttpHttpClient, configuration);
        TestModel testModel = new TestModel();
        testModel.setTestClass(TEST_CLASS);
        testModel.setTestName("shouldSendTestStartedEvent");

        String url = HTTP_LOCALHOST_8080 + "/events/testStarted";
        eventService.testStarted(testModel, RUN_ID);

        verify(basicHttpHttpClient).post(eq(url), bodyCaptor.capture());

        String bodyValue = bodyCaptor.getValue();
        TestStarted testStarted = objectMapper.readValue(bodyValue, TestStarted.class);

        assertThat(testStarted.getRunId()).isEqualTo(RUN_ID);
        assertThat(testStarted.getTest()).isEqualTo(testModel);
    }

    @Test
    public void shouldSendTestFinishedEvent() throws IOException, SnitcherException {
        EventService eventService = new EventService(basicHttpHttpClient, configuration);
        TestModel testModel = new TestModel();
        testModel.setTestClass(TEST_CLASS);
        testModel.setTestName("shouldSendTestFinishedEvent");

        String url = HTTP_LOCALHOST_8080 + "/events/testFinished";
        eventService.testFinished(testModel, RUN_ID);

        verify(basicHttpHttpClient).post(eq(url), bodyCaptor.capture());

        String bodyValue = bodyCaptor.getValue();
        TestFinished testStarted = objectMapper.readValue(bodyValue, TestFinished.class);

        assertThat(testStarted.getRunId()).isEqualTo(RUN_ID);
        assertThat(testStarted.getTest()).isEqualTo(testModel);
    }
}