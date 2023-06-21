package com.techbank.cqrs.core.events;

import com.techbank.cqrs.core.messages.Message;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEvent extends Message {
    private long version;
}
