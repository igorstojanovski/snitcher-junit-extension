package co.igorski.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=RunStarted.class, name = "RunStarted"),
})
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @NotNull
    private Date timestamp;
}
