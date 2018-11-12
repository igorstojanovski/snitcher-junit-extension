package co.igorski.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TestFinished.class, name = "TestFinished"),
        @JsonSubTypes.Type(value = TestStarted.class, name = "TestStarted"),
        @JsonSubTypes.Type(value = RunStarted.class, name = "RunStarted")
})
@Getter
@Setter
@NoArgsConstructor
public class Event {
    private Date timestamp;
}
