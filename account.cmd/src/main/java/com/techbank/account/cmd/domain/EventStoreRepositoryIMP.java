package com.techbank.account.cmd.domain;

import com.techbank.cqrs.core.events.EventModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EventStoreRepositoryIMP implements EventStoreRepository{

    private final EntityManager entityManager;

    @Autowired
    public EventStoreRepositoryIMP(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<EventModel> findByAggregateIdentifier(String aggregateIdentifier) {
        TypedQuery<EventModel> query = this.entityManager.createQuery("SELECT model " +
                "from EventModel model " +
                "WHERE aggregateIdentifier = :aggregateIdentifier", EventModel.class);
        query.setParameter("aggregateIdentifier", aggregateIdentifier);

        return query.getResultList();

    }

    @Override
    public EventModel merge(EventModel event) {
        return this.entityManager.merge(event);
    }
}
