package co.igorski.services;

import co.igorski.exceptions.SnitcherException;
import co.igorski.model.Outcome;
import co.igorski.model.Status;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stubs.classes.DummyTest;
import stubs.exceptions.DummyException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CentralCommitteeServiceTest {
    @Mock
    private LoginService loginService;
    @Mock
    private EventService eventService;
    private TestModel test;
    private Map<String, TestModel> tests;
    private Launcher launcher;
    private LauncherDiscoveryRequest request;
    @Captor
    private ArgumentCaptor<TestModel> testModelCaptor;

    @BeforeEach
    public void beforeEach() {
        request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(selectPackage("stubs.classes"),
                        selectClass(DummyTest.class))
                .filters(includeClassNamePatterns(".*Test")).build();

        launcher = LauncherFactory.create();

        test = new TestModel();
        test.setTestName("shouldReturnCorrectResult");
        test.setTestClass("stubs.classes.DummyTest");

        tests = new HashMap<>();
        tests.put(test.uniqueId(), test);
    }

    @Test
    public void shouldLoginAfterTestPlanExecutionIsStarted() {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);
        service.testPlanExecutionStarted(testPlan);

        verify(loginService).login();
    }

    @Test
    public void shouldSendTestPlanStartedEvent() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);
        User user = new User();

        when(loginService.login()).thenReturn(user);

        service.testPlanExecutionStarted(testPlan);

        verify(eventService).testRunStarted(tests, user);
    }

    @Test
    public void shouldNotSendEventsIfLoginWasNotSuccessful() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);

        when(loginService.login()).thenReturn(null);

        service.testPlanExecutionStarted(testPlan);

        verify(eventService, never()).testRunStarted(any(), any());
    }

    @Test
    public void shouldSendTestPlanFinishedEvent() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);

        User user = new User();
        when(loginService.login()).thenReturn(user);
        TestRun testRun = new TestRun();
        testRun.setId(1L);
        when(eventService.testRunStarted(tests, user)).thenReturn(testRun);
        service.testPlanExecutionStarted(testPlan);

        service.testPlanExecutionFinished(testPlan);

        verify(eventService).testRunFinished(1L);
    }


    @Test
    public void shouldSendTestStartedEvent() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);

        User user = new User();
        when(loginService.login()).thenReturn(user);
        TestRun testRun = new TestRun();
        testRun.setId(1L);
        when(eventService.testRunStarted(tests, user)).thenReturn(testRun);
        service.testPlanExecutionStarted(testPlan);

        TestIdentifier testIdentifier = getTestIdentifier();
        service.executionStarted(testIdentifier);

        verify(eventService).testStarted(test, testRun.getId());
    }

    @Test
    public void shouldSendTestFinishedEventWithSuccess() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);

        User user = new User();
        when(loginService.login()).thenReturn(user);
        TestRun testRun = new TestRun();
        testRun.setId(1L);
        when(eventService.testRunStarted(tests, user)).thenReturn(testRun);
        service.testPlanExecutionStarted(testPlan);

        TestIdentifier testIdentifier = getTestIdentifier();
        service.executionFinished(testIdentifier, TestExecutionResult.successful());

        verify(eventService).testFinished(testModelCaptor.capture(), eq(testRun.getId()));
        TestModel captured = testModelCaptor.getValue();

        assertThat(captured.getStatus()).isEqualTo(Status.FINISHED);
        assertThat(captured.getOutcome()).isEqualTo(Outcome.PASSED);
    }

    @Test
    public void shouldSendTestFinishedEventWithFailure() throws SnitcherException {
        TestPlan testPlan = launcher.discover(request);
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);

        User user = new User();
        when(loginService.login()).thenReturn(user);
        TestRun testRun = new TestRun();
        testRun.setId(1L);
        when(eventService.testRunStarted(tests, user)).thenReturn(testRun);
        service.testPlanExecutionStarted(testPlan);

        TestIdentifier testIdentifier = getTestIdentifier();
        service.executionFinished(testIdentifier, TestExecutionResult.failed(new DummyException()));

        verify(eventService).testFinished(testModelCaptor.capture(), eq(testRun.getId()));
        TestModel captured = testModelCaptor.getValue();

        assertThat(captured.getStatus()).isEqualTo(Status.FINISHED);
        assertThat(captured.getOutcome()).isEqualTo(Outcome.FAILED);
        assertThat(captured.getError()).contains("co.igorski.services.CentralCommitteeServiceTest" +
                ".shouldSendTestFinishedEventWithFailure(CentralCommitteeServiceTest.java:");
    }

    private TestIdentifier getTestIdentifier() {
        TestPlan testPlan = launcher.discover(request);
        Iterator<TestIdentifier> rootIterator = testPlan.getRoots().iterator();

        TestIdentifier jupiterRoot = null;
        while (rootIterator.hasNext()) {
            TestIdentifier tmp = rootIterator.next();
            if (tmp.getUniqueId().equals("[engine:junit-jupiter]")) {
                jupiterRoot = tmp;
                break;
            }
        }

        Set<TestIdentifier> children = testPlan.getChildren(jupiterRoot);
        TestIdentifier classIdentifier = children.iterator().next();
        return testPlan.getChildren(classIdentifier).iterator().next();
    }
}