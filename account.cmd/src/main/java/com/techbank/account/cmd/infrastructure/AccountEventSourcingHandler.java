package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handler.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

    private final EventStore eventStore;


    public AccountEventSourcingHandler(@Autowired EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(AggregateRoot aggregateRoot) {

        this.eventStore.saveEvents(aggregateRoot.getId(), aggregateRoot.getChanges(), aggregateRoot.getVersion());
        aggregateRoot.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getById(String id) {
        var aggregate = new AccountAggregate();
        var events = eventStore.getEvents(id);
        if(events!= null &&  !events.isEmpty()){
            aggregate.getRoot().replayEvents(events);
            var aggregateLatestVersion = events.stream().map(x -> x.getVersion()).max(Comparator.naturalOrder());
            aggregate.getRoot().setVersion(aggregateLatestVersion.get());
        }
        return aggregate;
    }
}
