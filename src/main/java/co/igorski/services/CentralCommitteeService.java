package co.igorski.services;

import co.igorski.model.TestModel;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The central service that will act as a facade.
 */
public class CentralCommitteeService implements TestExecutionListener {

    private final LoginService loginService;
    private final EventService eventService;

    /**
     * Needs a login service and an event service.
     *
     * @param loginService handles the login
     * @param eventService handles sending the events
     */
    public CentralCommitteeService(LoginService loginService, EventService eventService) {
        this.loginService = loginService;
        this.eventService = eventService;
    }

    /**
     * Has two main functions:<br>
     * 1. to login
     * 2. to create a list of all tests and send it to {@link EventService#testRunStarted(List)}
     *
     * @param testPlan the test plan retrieved from JUnit
     */
    public void testPlanExecutionStarted(TestPlan testPlan) {
        loginService.login();
        eventService.testRunStarted(collectAllTests(testPlan));
    }

    private List<TestModel> collectAllTests(TestPlan testPlan) {
        List<TestModel> testModelList = new ArrayList<>();

        Set<TestIdentifier> roots = testPlan.getRoots();
        for(TestIdentifier root : roots) {
            Set<TestIdentifier> children = testPlan.getChildren(root.getUniqueId());
            addTests(testModelList, children, testPlan);
        }

        return testModelList;
    }

    private void addTests(List<TestModel> testModelList, Set<TestIdentifier> children, TestPlan testPlan) {
        for(TestIdentifier testIdentifier : children) {
            if(testIdentifier.getType() == TestDescriptor.Type.TEST) {
                getTestModel(testIdentifier).ifPresent(testModelList::add);
            } else if (testIdentifier.getType() == TestDescriptor.Type.CONTAINER) {
                addTests(testModelList, testPlan.getChildren(testIdentifier), testPlan);
            }
        }
    }

    /**
     * Will create a {@link TestModel} object only if the {@link TestIdentifier} is of
     * type {@link org.junit.platform.engine.TestDescriptor.Type.TEST} and the source is of type {@link MethodSource}.
     *
     * @param testIdentifier the test identifier to create a TestModel from
     * @return optional of TestModel
     */
    private Optional<TestModel> getTestModel(TestIdentifier testIdentifier) {
        Optional<TestModel> optional = Optional.empty();
        Optional<TestSource> source = testIdentifier.getSource();
        if(source.isPresent() && source.get() instanceof MethodSource) {
            TestModel testModel = new TestModel();
            MethodSource methodSource = (MethodSource) source.get();
            testModel.setTestName(methodSource.getMethodName());
            testModel.setTestClass(methodSource.getClassName());
            optional = Optional.of(testModel);
        }

        return optional;
    }

}
