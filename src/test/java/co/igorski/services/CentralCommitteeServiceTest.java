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
    @Mock
    private LoginService loginService;
    @Mock
    private EventService eventService;
    private TestModel test;
    private List<TestModel> tests;
    private Launcher launcher;
    private LauncherDiscoveryRequest request;

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

        tests = new ArrayList<>();
        tests.add(test);
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