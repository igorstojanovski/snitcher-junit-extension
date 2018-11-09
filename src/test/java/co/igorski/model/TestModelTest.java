package co.igorski.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestModelTest {

    @Test
    public void defaultStatusShouldBePending() {
        TestModel testModel = new TestModel();
        assertThat(testModel.getStatus()).isEqualTo(Status.PENDING);
    }

}