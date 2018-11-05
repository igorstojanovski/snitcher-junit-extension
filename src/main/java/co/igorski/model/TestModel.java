package co.igorski.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestModel {
    private String testName;
    private String testClass;
    private String testPackage;

    public String getFullName() {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add(testPackage).add(testClass).add(testName);
        return joiner.toString();
    }
}
