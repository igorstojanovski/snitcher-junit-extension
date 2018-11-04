package co.igorski.services;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class CentralCommitteeService implements TestExecutionListener {

    private final LoginService loginService;

    public CentralCommitteeService(LoginService loginService) {
        this.loginService = loginService;
    }

    public void testPlanExecutionStarted(TestPlan testPlan) {
        loginService.login();
    }
}
