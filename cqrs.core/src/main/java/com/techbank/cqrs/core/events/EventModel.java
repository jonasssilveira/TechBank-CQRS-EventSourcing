package com.techbank.cqrs.core.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Document(collection = "eventStore")
public class EventModel {

    @Id
    private String id;
    private LocalDate timeStamp;
    private String aggregateIdentifier;
    private String aggregateType;
    private long version;
    private BaseEvent eventData;

}
