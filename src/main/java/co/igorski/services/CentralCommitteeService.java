package co.igorski.services;

import co.igorski.exceptions.SnitcherException;
import co.igorski.model.Outcome;
import co.igorski.model.Status;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * The central service that will act as a facade.
 */
public class CentralCommitteeService implements TestExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(CentralCommitteeService.class);
    private final LoginService loginService;
    private final EventService eventService;
    private boolean skipExecution;
    private TestRun testRun;
    private Map<String, TestModel> tests;

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
     * 2. to create a list of all tests and send it to {@link EventService#testRunStarted(Map, User)}
     *
     * @param testPlan the test plan retrieved from JUnit
     */
    public void testPlanExecutionStarted(TestPlan testPlan) {
        User user = loginService.login();
        try {
            tests = collectAllTests(testPlan);
            testRun = eventService.testRunStarted(tests, user);
        } catch (SnitcherException e) {
            skipExecution = true;
        }
    }

    private Map<String, TestModel> collectAllTests(TestPlan testPlan) {
        Map<String, TestModel> testModelList = new HashMap<>();

        Set<TestIdentifier> roots = testPlan.getRoots();
        for (TestIdentifier root : roots) {
            Set<TestIdentifier> children = testPlan.getChildren(root.getUniqueId());
            addTests(testModelList, children, testPlan);
        }

        return testModelList;
    }

    private void addTests(Map<String, TestModel> tests, Set<TestIdentifier> children, TestPlan testPlan) {
        for (TestIdentifier testIdentifier : children) {
            if (testIdentifier.getType() == TestDescriptor.Type.TEST) {
                createTest(testIdentifier).ifPresent(testModel -> tests.put(testModel.uniqueId(), testModel));
            } else if (testIdentifier.getType() == TestDescriptor.Type.CONTAINER) {
                addTests(tests, testPlan.getChildren(testIdentifier), testPlan);
            }
        }
    }

    /**
     * Will create a {@link TestModel} object only if the {@link TestIdentifier} is of
     * type {@link org.junit.platform.engine.TestDescriptor.Type} and the source is of type {@link MethodSource}.
     *
     * @param testIdentifier the test identifier to create a TestModel from
     * @return optional of TestModel
     */
    private Optional<TestModel> createTest(TestIdentifier testIdentifier) {
        Optional<TestModel> optional = Optional.empty();
        Optional<TestSource> source = testIdentifier.getSource();
        if (source.isPresent() && source.get() instanceof MethodSource) {
            TestModel testModel = new TestModel();
            MethodSource methodSource = (MethodSource) source.get();
            testModel.setTestName(methodSource.getMethodName());
            testModel.setTestClass(methodSource.getClassName());
            optional = Optional.of(testModel);
        }

        return optional;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        TestModel testModel = tests.get(getUniqueId(testIdentifier));
        testModel.setStatus(Status.RUNNING);
        try {
            eventService.testStarted(testModel, testRun.getId());
        } catch (SnitcherException e) {
            LOG.error("Error sending test started event", e);
        }
    }

    private static String getExceptionMessageChain(Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        while (throwable != null) {
            if (throwable.getMessage() != null) {
                joiner.add(throwable.getMessage());
            }
            StackTraceElement[] elements = throwable.getStackTrace();
            for (StackTraceElement element : elements) {
                joiner.add(element.toString());
            }

            throwable = throwable.getCause();
        }
        return joiner.toString();
    }

    private String getUniqueId(TestIdentifier testIdentifier) {
        MethodSource methodSource = (MethodSource) testIdentifier.getSource().get();
        return methodSource.getClassName() + '.' + methodSource.getMethodName();
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        TestModel test = tests.get(getUniqueId(testIdentifier));
        test.setStatus(Status.FINISHED);

        if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
            test.setOutcome(Outcome.PASSED);
        } else {
            test.setOutcome(Outcome.FAILED);
            testExecutionResult.getThrowable().ifPresent(t -> test.setError(getExceptionMessageChain(t)));
        }

        eventService.testFinished(test, testRun.getId());
    }
}
