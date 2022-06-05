package com.eventu.resource;

import com.eventu.repository.EventRepository;
import com.eventu.repository.PersonRepository;
import com.eventu.vo.AStatus;
import com.eventu.vo.Event;
import com.eventu.vo.EventError;
import com.eventu.vo.Person;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/party/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventRepository repository;

    @Inject
    PersonRepository personRepository;

    @POST
    @Path("/{person_id}/event")
    public Uni<Response> create(Event event, @PathParam("person_id") String personId){
        //Check if personId exist.
        //If person does not exist
        //      then return AStatus error
        //ELSE
        //      Create event and eventId in the fetched person

         return personRepository.getPersonById(personId)
                 .onItem().ifNull()
                 .fail().onFailure()
                        .recoverWithItem( f-> {
                                AStatus status =  createErrorStatus(f.getMessage());
                                return (Person) status;

        }).map(r-> Response.ok(r).build()) ;


        //return response;
         /*event.persist().onItem()
               // .call(upda)
                .transformToUni(resp -> Uni.createFrom().item(Response.ok().entity(resp).build()));*/


    }

    private Person createErrorStatus(String statusDesc){

        Person person = Person.builder().build();
        person.setStatus("Error");
        person.setError(
                EventError.builder()
                        .errorDesc(statusDesc)
                        .build()
        );
        return person;
    }

}
