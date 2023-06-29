package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.cqrs.core.events.EventModel;
import com.techbank.cqrs.core.exception.AggregateNotFoundException;
import com.techbank.cqrs.core.exception.ConcurrencyExpection;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {

    private final EventStoreRepository eventStoreRepository;


    public AccountEventStore(@Autowired EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }


    @Override
    public void saveEvents(String aggregateId, Iterable<BaseEvent> events, long expectedVersion) {

        var eventStream = this.eventStoreRepository.findByAggregateIdentifier(aggregateId);

        if(expectedVersion != -1 && eventStream.get(eventStream.size() -1).getVersion()!=expectedVersion){
            throw new ConcurrencyExpection();
        }

        var version = expectedVersion;

        for(var event: events){
            version++;
            event.setVersion(version);
            var eventModel = EventModel.builder().timeStamp(LocalDate.now())
                    .aggregateIdentifier(aggregateId)
                    .aggregateType(AccountAggregate.class.getTypeName())
                    .version(version)
                    .eventType(event.getClass().getTypeName())
                    .eventData(event)
                    .build();

            if(this.eventStoreRepository.merge(eventModel)!= null){
                //send to kafka
            }
        }


    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        List<EventModel> eventModels = this.eventStoreRepository.findByAggregateIdentifier(aggregateId);

        if(eventModels == null|| eventModels.isEmpty())
            throw new AggregateNotFoundException("Incorrectaccount Id  provided!");
        return eventModels.stream().map(event -> event.getEventData()).collect(Collectors.toList());

    }
}
