package co.igorski.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestRun {
    private Long id;
    private List<TestModel> tests;
    private Date startTime;
    private Date endTime;
}
