package co.igorski.model.internal;

import co.igorski.model.TestModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

class TestModelTest {

    @Test
    public void shouldReturnFullName() {
        TestModel test = new TestModel("shouldReturnFullName", "co.igorski.TestModelTest");
        assertThat(test.getFullName()).isEqualTo("co.igorski.TestModelTest.shouldReturnFullName");
    }
}