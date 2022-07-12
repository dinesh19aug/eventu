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

    public Uni<SubEvent> updateSubEvent(SubEvent existingSE, SubEvent updatedSE){
        existingSE.setEventName(updatedSE.getEventName());
        existingSE.setEndTime(updatedSE.getEndTime());
        existingSE.setEventEndDate(updatedSE.getEventEndDate());
        existingSE.setEventDescription(updatedSE.getEventDescription());
        existingSE.setEventUrl(updatedSE.getEventUrl());
        existingSE.setEventStartDate(updatedSE.getEventStartDate());
        existingSE.setOrganizationName(updatedSE.getOrganizationName());
        existingSE.setStartTime(updatedSE.getStartTime());
        existingSE.setSpeakerName(updatedSE.getSpeakerName());
        existingSE.setOrganizerName(updatedSE.getOrganizerName());
        existingSE.setOrgUrl(updatedSE.getOrgUrl());
        return update(existingSE).onFailure().transform(f-> new BusinessException(createErrorMessage(f, "Unable to update the Subevent"),f));

    }

}
