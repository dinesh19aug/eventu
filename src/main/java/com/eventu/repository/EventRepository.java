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
                   if(eventObj!=null){
                       if(eventObj.subEventSummaryMap==null){
                           eventObj.subEventSummaryMap=new HashMap<>();
                       }
                       SubEventSummary subEventSummary = createSubEventSummry(subEvent);
                       eventObj.getSubEventSummaryMap().put(subEvent.getId().toString(),subEventSummary);
                       //TODO - What is the purpose of this?
                       eventObj.eventName = event.getEventName();
                       return this.persistOrUpdate(eventObj)
                               .onItem().transformToUni(e -> Uni.createFrom().item(e))
                               .onFailure().transform(f->

                                       new BusinessException(createErrorMessage(f, "Cannot update the event"), f));
                   }
                   //TODO if event Object is not found then This should return null
                   return Uni.createFrom().item(event);
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
