package co.igorski.services;

import co.igorski.client.HttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import co.igorski.model.events.Event;
import co.igorski.model.events.RunFinished;
import co.igorski.model.events.RunStarted;
import co.igorski.model.events.TestDisabled;
import co.igorski.model.events.TestFinished;
import co.igorski.model.events.TestStarted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class EventService {
    private final HttpClient httpClient;
    private final Configuration configuration;
    private final ObjectMapper objectMapper = new ObjectMapper();

    EventService(HttpClient httpClient, Configuration configuration) {
        this.httpClient = httpClient;
        this.configuration = configuration;
    }

    TestRun testRunStarted(Map<String, TestModel> tests, User user) throws SnitcherException {

        RunStarted runStarted = new RunStarted();
        runStarted.setUser(user);
        runStarted.setTests(new ArrayList<>(tests.values()));
        runStarted.setTimestamp(new Date());

        return getTestRunResponse("/events/runStarted", runStarted);
    }

    TestRun testRunFinished(Long runId) throws SnitcherException {

        RunFinished runFinished = new RunFinished();
        runFinished.setRunId(runId);
        runFinished.setTimestamp(new Date());
        return getTestRunResponse("/events/runFinished", runFinished);
    }

    private TestRun getTestRunResponse(String endpoint, Event runEvent) throws SnitcherException {
        TestRun testRunResponse;
        try {
            String body = objectMapper.writeValueAsString(runEvent);
            String response = httpClient.post(configuration.getServerUrl() + endpoint, body);
            testRunResponse = objectMapper.readValue(response, TestRun.class);
        } catch (JsonProcessingException e) {
            throw new SnitcherException("Error when serializing Run event object to JSON", e);
        } catch (IOException e) {
            throw new SnitcherException("Error when deserializing JSON to a Run event", e);
        }
        return testRunResponse;
    }

    void testStarted(TestModel testModel, Long runId) throws SnitcherException {

        TestStarted testStarted = new TestStarted();
        testStarted.setTest(testModel);
        testStarted.setTimestamp(new Date());
        testStarted.setRunId(runId);
        sendPost("/events/testStarted", testStarted);
    }

    void testFinished(TestModel testModel, Long runId) throws SnitcherException {

        TestFinished testFinished = new TestFinished();
        testFinished.setTest(testModel);
        testFinished.setTimestamp(new Date());
        testFinished.setRunId(runId);
        sendPost("/events/testFinished", testFinished);
    }

    private void sendPost(String endpoint, Event event) throws SnitcherException {
        try {
            httpClient.post(configuration.getServerUrl() + endpoint, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new SnitcherException("Error when serializing object to JSON", e);
        } catch (IOException e) {
            throw new SnitcherException("Error when sending Event request.", e);
        }
    }

    void testDisabled(TestModel testModel, Long runId) throws SnitcherException {

        TestDisabled testDisabled = new TestDisabled();
        testDisabled.setTest(testModel);
        testDisabled.setTimestamp(new Date());
        testDisabled.setRunId(runId);
        sendPost("/events/testDisabled", testDisabled);
    }
}
