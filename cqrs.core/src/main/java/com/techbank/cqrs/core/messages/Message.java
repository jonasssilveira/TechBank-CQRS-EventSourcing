package com.techbank.cqrs.core.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Message{

    @Getter
    @Setter
    String id;

    @Getter
    @Setter
    long version;

}
