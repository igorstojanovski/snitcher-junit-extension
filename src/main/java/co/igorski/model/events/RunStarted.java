package co.igorski.model.events;

import co.igorski.model.Organization;
import co.igorski.model.TestModel;
import co.igorski.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RunStarted extends Event {
    private Organization organization;
    private User user;
    private List<TestModel> tests;
}
