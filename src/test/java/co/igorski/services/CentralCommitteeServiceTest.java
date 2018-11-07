package co.igorski.services;

import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stubs.classes.DummyTest;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    public void beforeEach() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(selectPackage("stubs.classes"),
                        selectClass(DummyTest.class))
                .filters(includeClassNamePatterns(".*Test")).build();

        Launcher launcher = LauncherFactory.create();
        testPlan = launcher.discover(request);
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
        when(loginService.login()).thenReturn(true);

        service.testPlanExecutionStarted(testPlan);

        List<TestModel> tests = new ArrayList<>();

        TestModel testModel = new TestModel("shouldReturnCorrectResult", "stubs.classes.DummyTest");
        tests.add(testModel);

        verify(eventService).testRunStarted(tests);
    }
}