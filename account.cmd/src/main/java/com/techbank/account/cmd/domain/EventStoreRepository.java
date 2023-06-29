package com.techbank.account.cmd.domain;

 import com.techbank.cqrs.core.events.EventModel;
 import org.springframework.stereotype.Repository;

 import java.util.List;

@Repository
public interface EventStoreRepository {

    List<EventModel> findByAggregateIdentifier(String aggregateIdentifier);

    EventModel merge(EventModel eventModel);
}
