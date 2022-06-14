package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.Event;
import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventRepository implements ReactivePanacheMongoRepository<Event> {
    public Uni<Event> create(Event event, ObjectId personId){
        event.setPersonId(personId);
        return persist(event)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f), f)) ;

    }

    private String createErrorMessage(Throwable ex){
        if(((MongoWriteException)ex).getCode() == 11000){
            return "Event already exists";
        }
        return "Unknown error";
    }
}
