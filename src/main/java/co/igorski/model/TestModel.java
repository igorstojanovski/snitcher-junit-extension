package co.igorski.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestModel {
    private String testName;
    private String testClass;

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
