package com.eventu.resource;

import com.eventu.repository.EventRepository;
import com.eventu.repository.SubeventRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.Event;
import com.eventu.vo.SubEvent;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/party/event")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SubEventResource implements IResource {

    @Inject
    SubeventRepository subeventRepository;

    @Inject
    EventRepository eventRepository;

    @POST
    @Path("/{event_id}/subEvent")
    public Uni<Response> create(SubEvent subEvent, @PathParam("event_id") String eventId){
        //Check if eventId exist.
       Uni<Event> uniEvent = eventRepository.findById(new ObjectId(eventId));

        return uniEvent.onItem()
                .transformToUni(event ->  {
                    if( event != null){
                        return subeventRepository.create(subEvent, event.getId())
                                .onItem()
                                .transform(se -> {
                                    //updateEvent(event.id, se);
                                        eventRepository.addEventSummary(event,se).subscribe().with(result -> {
                                            System.out.println("Inserted " + result);
                                                    Response.ok().entity(se).build();},
                                                failure -> System.out.println("Error: " + failure));


                                    return Response.ok().entity(se).build();

                                })
                                .onFailure()
                                .recoverWithItem(failure -> {
                                    AStatus status =  createErrorStatus(failure.getMessage());
                                    return Response.serverError().entity(status).build();
                                });
                }else {
                        AStatus status =  createErrorStatus("Event does not exist");
                        return Uni.createFrom().item( Response.serverError().entity(status).build());
                    }

                });


    }

    private void updateEvent(ObjectId eventId, SubEvent se) {
        Uni<Event> uniEvent = eventRepository.findById(eventId);
         uniEvent.onItem()
                .transformToUni(eventObj ->  {
                    if( eventObj != null){

                        eventObj.eventName = "Newly Updated";
                        return eventRepository.update(eventObj)
                                .onItem().transform(e -> Response.ok().entity(e).build())
                                .onFailure()
                                .recoverWithItem(failure -> {
                                    AStatus status =  createErrorStatus(failure.getMessage());
                                    return Response.serverError().entity(status).build();
                                });
                    }else {
                        AStatus status =  createErrorStatus("Person does not exist");
                        return Uni.createFrom().item( Response.serverError().entity(status).build());
                    }

                });
    }

}
