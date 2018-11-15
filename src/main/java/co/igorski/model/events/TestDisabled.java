package co.igorski.model.events;

import co.igorski.model.TestModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestDisabled extends Event {

    private Long runId;
    private TestModel test;
}