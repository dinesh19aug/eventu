package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.SubEvent;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubeventRepository implements ReactivePanacheMongoRepository<SubEvent>, IRepository  {
    public Uni<SubEvent> create(SubEvent subEvent, ObjectId eventId) {
        subEvent.setEventId(eventId);

        return persist(subEvent).onFailure().transform(f-> new BusinessException(createErrorMessage(f, "SubEvent already exists"),f));

    }

}
