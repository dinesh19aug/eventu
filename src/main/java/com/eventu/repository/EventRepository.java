package com.eventu.repository;

import com.eventu.exception.BusinessException;
import com.eventu.vo.*;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;

@ApplicationScoped
public class EventRepository implements ReactivePanacheMongoRepository<Event>, IRepository {
    public Uni<Event> create(Event event, ObjectId personId){
        event.setPersonId(personId);
        return persist(event)
                .onFailure().transform(f->  new BusinessException(createErrorMessage(f, "Event already exists"), f)) ;

    }

    public Uni<Event> addEventSummary(Event event, SubEvent subEvent){
       Uni<Event> uniEvent = this.findById(event.getId());
       return uniEvent.onItem()
               .transformToUni(eventObj->{

                       if(eventObj.subEventSummaryMap==null){
                           eventObj.subEventSummaryMap=new HashMap<>();
                       }
                       SubEventSummary subEventSummary = createSubEventSummry(subEvent);
                       eventObj.getSubEventSummaryMap().put(subEvent.getId().toString(),subEventSummary);
                       return this.persistOrUpdate(eventObj)
                               .onItem().transformToUni(e -> Uni.createFrom().item(e))
                               .onFailure().transform(f->
                                       new BusinessException(createErrorMessage(f, "Cannot update the event"), f));
               });
    }
    public Uni<Event> addAddress(Event event, Address address){
        Uni<Event> uniEvent = this.findById(event.getId());
        return uniEvent.onItem()
                .transformToUni(eventObj->{
                    eventObj.setVenueAddress(address.getId());
                    return this.persistOrUpdate(eventObj)
                            .onItem().transformToUni(e -> Uni.createFrom().item(e))
                            .onFailure().transform(f->
                                    new BusinessException(createErrorMessage(f, "Cannot update the address to event"), f));
                });
    }


    private SubEventSummary createSubEventSummry(SubEvent subEvent) {
        SubEventSummary subEventSummary = new SubEventSummary();
        subEventSummary.setEventName(subEvent.getEventName());
        subEventSummary.setEventStartDate(subEvent.getEventStartDate());
        subEventSummary.setEventEndDate(subEvent.getEventEndDate());
        subEventSummary.setStartTime(subEvent.getStartTime());
        subEventSummary.setEndTime(subEvent.getEndTime());
        return subEventSummary;
    }


}
