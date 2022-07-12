package com.eventu.resource;

import com.eventu.repository.EventRepository;
import com.eventu.repository.SubeventRepository;
import com.eventu.vo.Event;
import com.eventu.vo.SubEvent;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
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
                                        eventRepository.addEventSummary(event,se)
                                                .subscribe().with(result ->
                                                    Log.info("Updated eventId --> " + eventId ),
                                                failure ->  Log.info("Error updating eventId: " + eventId + " | for subevent: " + subEvent.getId()));
                                    return Response.ok().entity(se).build();
                                })
                                .onFailure()
                                .recoverWithUni(failure-> getErrorResponse(failure.getMessage()));
                }else {
                        return getErrorResponse("Event does not exist");

                    }
                });
    }

    @GET
    @Path("{event_id}/subEvent/{subEvent_id}")
    public Uni<Response> details( @PathParam("event_id") String eventId, @PathParam("subEvent_id") String subEventId){

        Uni<SubEvent> uniSubEvent = subeventRepository
                .find("_id = :id and eventId = :eventId", Parameters.with("id", new ObjectId(subEventId)).and("eventId", new ObjectId(eventId)).map() )
                .firstResult();

        return uniSubEvent.onItem()
                .transformToUni(se -> {
                    if(se!=null) {
                        return Uni.createFrom().item( Response.ok().entity(se).build());
                    }else{
                        return getErrorResponse("Sub Event does not exist");
                    }

                })
                .onFailure()
                .recoverWithUni(failure-> getErrorResponse(failure.getMessage()));
    }



    @PUT
    @Path("{event_id}/subEvent/{subEvent_id}")
    public Uni<Response> update(@PathParam("event_id") String eventId, @PathParam("subEvent_id") String subEventId, SubEvent subEvent){
        Uni<SubEvent> uniSubEvent = subeventRepository
                .find("_id = :id and eventId = :eventId", Parameters.with("id", new ObjectId(subEventId)).and("eventId", new ObjectId(eventId)).map() )
                .firstResult();
        return uniSubEvent.onItem()
                .transformToUni(se-> {
                    if(se!=null){
                         return updateSubEvent(se,subEvent);
                    }else{
                        return getErrorResponse("Sub Event does not exist");

                    }
                }).onFailure().recoverWithUni(failure-> getErrorResponse(failure.getMessage()));

    }

    private Uni<Response> updateSubEvent(SubEvent se, SubEvent subEvent) {
        return subeventRepository.updateSubEvent(se, subEvent)
               .onItem().transformToUni(updatedSe -> Uni.createFrom().item(Response.ok().entity(updatedSe).build()));
    }

    @DELETE
    @Path("{event_id}/subEvent/{subEvent_id}")
    public Uni<Response> delete(@PathParam("event_id") String eventId, @PathParam("subEvent_id") String subeventId){
        //TODO Write code for delete
        //TODO remove subEventId from event.summaryMap
        return null;
    }



}
