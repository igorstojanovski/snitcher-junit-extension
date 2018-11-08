package co.igorski.services;

import co.igorski.client.HttpClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import co.igorski.model.events.RunStarted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EventService {
    private final HttpClient httpClient;
    private final Configuration configuration;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventService(HttpClient httpClient, Configuration configuration) {
        this.httpClient = httpClient;
        this.configuration = configuration;
    }

    public TestRun testRunStarted(List<TestModel> tests, User user) throws SnitcherException {

        RunStarted runStarted = new RunStarted();
        runStarted.setUser(user);
        runStarted.setTests(tests);
        runStarted.setTimestamp(new Date());
        TestRun testRunResponse;
        try {
            String body = objectMapper.writeValueAsString(runStarted);
            String response = httpClient.post(configuration.getServerUrl() + "/events/runStarted", body);
            testRunResponse = objectMapper.readValue(response, TestRun.class);
        } catch (JsonProcessingException e) {
            throw new SnitcherException("Error when serializing TestRun object to JSON", e);
        } catch (IOException e) {
            throw new SnitcherException("Error when deserializing TestRun object to JSON", e);
        }

        return testRunResponse;
    }
}
