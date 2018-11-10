package co.igorski.services;

import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stubs.classes.DummyTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CentralCommitteeServiceTest {

    private TestPlan testPlan;

    @Mock
    private LoginService loginService;
    @Mock
    private EventService eventService;
    private TestModel test;
    private List<TestModel> tests;

    @BeforeEach
    public void beforeEach() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(selectPackage("stubs.classes"),
                        selectClass(DummyTest.class))
                .filters(includeClassNamePatterns(".*Test")).build();

        Launcher launcher = LauncherFactory.create();
        testPlan = launcher.discover(request);

        test = new TestModel();
        test.setTestName("shouldReturnCorrectResult");
        test.setTestClass("stubs.classes.DummyTest");

        tests = new ArrayList<>();
        tests.add(test);
    }

    @Test
    public void shouldLoginAfterTestPlanExecutionIsStarted() {
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);
        service.testPlanExecutionStarted(testPlan);

        verify(loginService).login();
    }

    @Test
    public void shouldSendTestPlanStartedEvent() throws SnitcherException {
        CentralCommitteeService service = new CentralCommitteeService(loginService, eventService);
        User user = new User();

        when(loginService.login()).thenReturn(user);

        service.testPlanExecutionStarted(testPlan);

        verify(eventService).testRunStarted(tests, user);
    }

    @Test
    public void shouldSendTestStartedEvent() throws SnitcherException {
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

    private TestIdentifier getTestIdentifier() {
        Iterator<TestIdentifier> rootIterator = testPlan.getRoots().iterator();

        rootIterator.next();
        TestIdentifier jupiterRoot = rootIterator.next();

        Set<TestIdentifier> children = testPlan.getChildren(jupiterRoot);
        TestIdentifier classIdentifier = children.iterator().next();
        return testPlan.getChildren(classIdentifier).iterator().next();
    }
}