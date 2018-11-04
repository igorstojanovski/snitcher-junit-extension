package co.igorski.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestPlan;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stubs.ConfigurationStub;
import stubs.TestDescriptionStub;
import stubs.classes.DummyTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CentralCommitteeServiceTest {

    private TestPlan testPlan;

    @Mock
    private LoginService loginService;

    @Test
    public void beforeEach() {
        ConfigurationStub configurationStub = new ConfigurationStub();
        TestDescriptionStub descriptionStub = new TestDescriptionStub(
                UniqueId.parse("[unique:id]"),
                DummyTest.class,
                configurationStub
        );

        List<TestDescriptor> descriptors = new ArrayList<>();
        descriptors.add(descriptionStub);

        testPlan = TestPlan.from(descriptors);
    }

    @Test
    public void shouldLoginAfterTestPlanExecutionIsStarted() {
        CentralCommitteeService service = new CentralCommitteeService(loginService);
        service.testPlanExecutionStarted(testPlan);

        verify(loginService).login();
    }


}