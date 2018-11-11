package co.igorski.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class TestModel {
    private String testName;
    private String testClass;
    private Status status = Status.PENDING;

    public String uniqueId() {
        return testClass + '.' + testName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestModel testModel = (TestModel) o;
        return Objects.equals(testName, testModel.testName) &&
                Objects.equals(testClass, testModel.testClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testName, testClass);
    }
}
